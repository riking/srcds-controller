/**
 * This file is part of the Source Dedicated Server Controller project.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or
 * combining it with srcds-controller (or a modified version of that library),
 * containing parts covered by the terms of GNU General Public License,
 * the licensors of this Program grant you additional permission to convey
 * the resulting work. {Corresponding Source for a non-source form of such a
 * combination shall include the source code for the parts of srcds-controller
 * used as well as that of the covered work.}
 *
 * For more information, please consult:
 *    <http://www.earthquake-clan.de/srcds/>
 *    <http://code.google.com/p/srcds-controller/>
 */
package de.eqc.srcds.core;

import static de.eqc.srcds.configuration.ConfigurationRegistry.AUTOSTART;
import static de.eqc.srcds.configuration.ConfigurationRegistry.FORBIDDEN_USER_PARAMETERS;
import static de.eqc.srcds.configuration.ConfigurationRegistry.SRCDS_EXECUTABLE;
import static de.eqc.srcds.configuration.ConfigurationRegistry.SRCDS_GAMETYPE;
import static de.eqc.srcds.configuration.ConfigurationRegistry.SRCDS_PARAMETERS;
import static de.eqc.srcds.configuration.ConfigurationRegistry.SRCDS_PATH;
import static de.eqc.srcds.configuration.ConfigurationRegistry.SRCDS_SERVER_PORT;
import static de.eqc.srcds.core.Constants.OUTPUT_READING_SHUTDOWN_TIMEOUT_MILLIS;
import static de.eqc.srcds.core.Constants.STARTUP_WAIT_TIME_MILLIS;

import java.io.File;
import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.eqc.srcds.configuration.Configuration;
import de.eqc.srcds.configuration.exceptions.ConfigurationException;
import de.eqc.srcds.enums.GameType;
import de.eqc.srcds.enums.OperatingSystem;
import de.eqc.srcds.enums.ServerState;
import de.eqc.srcds.exceptions.AlreadyRunningException;
import de.eqc.srcds.exceptions.NotRunningException;
import de.eqc.srcds.exceptions.StartupFailedException;

/**
 * Controls the server and provides therefore functions like start/stop/status
 * etc.
 * 
 * @author Hannes
 * @author Holger
 */
public class SourceDServerController extends AbstractServerController<Process> {

    private ServerOutputReader serverOutputReader;

    public SourceDServerController(final Configuration config) throws ConfigurationException {

	super("SRCDS server", config);

	try {
	    if (config.getValue(AUTOSTART, Boolean.class)) {
		setAutostart(config.getValue(AUTOSTART, Boolean.class));
	    }
	} catch (ConfigurationException e) {
	    log.warning(String.format("Autostart configuration is missing: %s",
				      e.getLocalizedMessage()));
	}
    }

    public ServerState getServerState() {

	ServerState state = ServerState.RUNNING;
	synchronized (getMutex()) {
	    if (server == null) {
		state = ServerState.STOPPED;
	    } else {
		int exitValue = -1;
		try {
		    exitValue = server.exitValue();
		    log.info("Server was terminated");
		} catch (Exception e) {
		    // Ignore
		}

		if (exitValue > -1) {
		    state = ServerState.TERMINATED;
		}
	    }
	}
	return state;
    }

    private List<String> parseCommandLine() throws ConfigurationException {

	final String executable = config.getValue(SRCDS_EXECUTABLE, String.class);

	final File srcdsPath = new File(config.getValue(SRCDS_PATH, String.class));
	final File srcdsExecutable = new File(srcdsPath.getPath() + File.separator + executable);
	if (!srcdsPath.exists()) {
	    throw new ConfigurationException(String.format("Unable to find SRCDS path: %s",
							   srcdsPath.getPath()));
	} else if (!srcdsPath.isDirectory()) {
	    throw new ConfigurationException(String.format("Configured SRCDS path %s is not a directory",
							   srcdsPath.getPath()));
	} else if (!srcdsExecutable.exists()) {
	    throw new ConfigurationException(String.format("Configured SRCDS executable %s does not exist",
							   srcdsExecutable.getPath()));
	} else if (srcdsExecutable.isDirectory()) {
	    throw new ConfigurationException(String.format("Configured SRCDS executable %s refers to a directory",
							   srcdsExecutable.getPath()));
	}

	final GameType gameType = getGameType();
	log.info(String.format("Game type is %s", gameType));

	final AbstractSequentialList<String> parameters =
		gameType.getImplementation().getParametersAsList();

	final int srcdsPort = config.getValue(SRCDS_SERVER_PORT, Integer.class);
	parameters.add(String.format("+hostport %d", srcdsPort));
	parameters.add("-norestart");
	if (OperatingSystem.getCurrent() == OperatingSystem.WINDOWS) {
	    parameters.add("-console");
	}

	final List<String> userParameters = parseUserParameters();
	for (int i = userParameters.size() - 1; i >= 0; i--) {
	    final String userParameter = userParameters.get(i).split(" ")[0];
	    if (FORBIDDEN_USER_PARAMETERS.contains(userParameter)) {
		userParameters.remove(i);
		log.warning(String.format("Forbidden user parameter %s ignored", userParameter));
	    }
	}

	parameters.addAll(userParameters);
	parameters.add(0, srcdsExecutable.getAbsolutePath());

	log.info(String.format("Process: %s", parameters.toString()));

	return parameters;
    }

    public GameType getGameType() throws ConfigurationException {

	return config.getValue(SRCDS_GAMETYPE, GameType.class);
    }

    private List<String> parseUserParameters() throws ConfigurationException {

	String userParametersString = config.getValue(SRCDS_PARAMETERS, String.class).trim();
	final List<String> userParameters = new LinkedList<String>();

	if (OperatingSystem.getCurrent() == OperatingSystem.LINUX) {
	    final List<String> plusParameterNames = new LinkedList<String>();
	    for (int i = 0; i < userParametersString.length(); i++) {
		final char chr = userParametersString.charAt(i);
		if (chr == '+' && userParametersString.lastIndexOf(' ') > i) {
		    final int pNameLength = userParametersString.substring(i + 1).indexOf(' ');
		    final int startOffset = i + 1;
		    final String pName =
			    userParametersString.substring(startOffset, startOffset + pNameLength);
		    plusParameterNames.add(pName);
		}
	    }

	    userParametersString = userParametersString.replaceAll("\\+", "-");
	    final String[] parts = userParametersString.split("-");
	    for (String part : parts) {
		if (!"".equals(part)) {
		    final String prefix = plusParameterNames.contains(part.split(" ")[0])
			    ? "+"
			    : "-";
		    userParameters.add(prefix + part.trim());
		}
	    }
	} else {
	    userParameters.addAll(Arrays.asList(userParametersString.split(" ")));
	}

	return userParameters;
    }

    @Override
    public void startServer() throws AlreadyRunningException, StartupFailedException,
	    ConfigurationException {

	synchronized (getMutex()) {
	    if (getServerState() != ServerState.RUNNING) {
		try {
		    final File srcdsPath = getSrcdsPath();

		    final ProcessBuilder processBuilder = new ProcessBuilder(parseCommandLine());

		    if (OperatingSystem.getCurrent() == OperatingSystem.LINUX) {
			processBuilder.environment().put("LD_LIBRARY_PATH",
							 String.format("%s%s%s",
								       ".",
								       File.pathSeparator,
								       "bin"));
		    }

		    processBuilder.redirectErrorStream(true);
		    processBuilder.directory(srcdsPath);
		    server = processBuilder.start();
		    
		    serverOutputReader = new ServerOutputReader(server.getInputStream());
		    serverOutputReader.start();

		    Thread.sleep(STARTUP_WAIT_TIME_MILLIS);

		    if (getServerState() != ServerState.RUNNING) {
			throw new StartupFailedException("Process was terminated during startup phase");
		    }
		} catch (Exception e) {
		    throw new StartupFailedException(String.format("Unable to start server: %s",
								   e.getLocalizedMessage()), e);
		}
	    } else {
		throw new AlreadyRunningException("Server is already running");
	    }
	}
    }

    public File getSrcdsPath() throws ConfigurationException {

	final File srcdsPath = new File(config.getValue(SRCDS_PATH, String.class));
	if (srcdsPath.exists()) {
	    log.info(String.format("SRCDS path is %s", srcdsPath.getPath()));
	} else {
	    throw new ConfigurationException(String.format("%s refers to the non-existent path %s",
							   SRCDS_PATH,
							   srcdsPath.getPath()));
	}
	return srcdsPath;
    }

    @Override
    public void stopServer() throws NotRunningException {

	synchronized (getMutex()) {
	    if (getServerState() != ServerState.RUNNING) {
		throw new NotRunningException("Server is not running");
	    } else {
		serverOutputReader.stopGraceful();
		try {
		    serverOutputReader.join(OUTPUT_READING_SHUTDOWN_TIMEOUT_MILLIS);
		} catch (InterruptedException e1) {
		    // Ignore
		}

		log.info("Destroying reference to process");
		server.destroy();
		try {
		    server.waitFor();
		} catch (InterruptedException e) {
		    // Ignore
		}
		server = null;
	    }
	}
    }

    /**
     * Gets a server output to read the last output log and register on output
     * events.
     * 
     * @return
     */
    public ServerOutput getServerOutput() {

	return this.serverOutputReader;
    }
}