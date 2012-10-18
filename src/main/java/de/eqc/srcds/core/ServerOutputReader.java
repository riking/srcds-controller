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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import de.eqc.srcds.core.logging.LogFactory;
import de.eqc.srcds.enums.OperatingSystem;

public class ServerOutputReader extends Thread implements ServerOutput {

    private static final int SAVE_LAST_LINES = 20;

    private static Logger log = LogFactory.getLogger(ServerOutputReader.class);

    private final transient InputStream inputStream;
    private final transient AtomicBoolean running = new AtomicBoolean(false);

    // we use a concurrent version of the deque because many threads may access
    // this object
    private final transient LinkedBlockingDeque<String> savedLogLines =
	    new LinkedBlockingDeque<String>(SAVE_LAST_LINES);
    // same here
    private final transient List<ProcessOutputObserver> outputObservers =
	    Collections.synchronizedList(new ArrayList<ProcessOutputObserver>(3));

    public ServerOutputReader(final InputStream inputStream) {

	setName(getClass().getSimpleName());
	this.running.set(false);
	this.inputStream = inputStream;
    }

    @Override
    public void run() {

	final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	running.set(true);

	if (OperatingSystem.getCurrent() == OperatingSystem.LINUX) {
	    log.info("Reading server output started");
	    try {
		String line;
		while ((line = reader.readLine()) != null && running.get()) {
		    // TODO: apply a filter here?
		    this.saveLogLine(line);
		    log.fine(line);
		}
		if (line != null) {
		    this.saveLogLine(line);
		    log.fine(line);
		}
	    } catch (IOException e) {
		log.info(String.format("Error while reading server output: %s",
				       e.getLocalizedMessage()));
	    }
	} else {
	    final String unsupportedMessage =
		    String.format("%s does currently not support your operating system.",
				  getClass().getSimpleName());
	    this.saveLogLine(unsupportedMessage);
	    log.info(unsupportedMessage);
	}

	log.info("Reading server output stopped");
	running.set(false);
    }

    /**
     * Saves the given output to the history (up to {@link #SAVE_LAST_LINES})
     * and notifies all observers.
     * 
     * @param line
     */
    private void saveLogLine(final String line) {

	if (this.savedLogLines.size() == SAVE_LAST_LINES) {
	    this.savedLogLines.removeFirst();
	}
	this.savedLogLines.add(line);
	this.notifyObservers(line);
    }

    public void stopGraceful() {

	unRegisterAllOnLogObservers();
	running.set(false);
    }

    public boolean isRunning() {

	return running.get();
    }

    /**
     * Notifies the registered observers about the new line of output.
     * 
     * @param newLine
     */
    private void notifyObservers(final String newLine) {

	synchronized (this.outputObservers) {
	    for (ProcessOutputObserver observer : this.outputObservers) {
		observer.outputHasChanged(newLine);
	    }
	}
    }

    /*
     * ServerOutput implementation
     */

    @Override
    public Collection<String> getLastLog() {

	return Collections.unmodifiableCollection(this.savedLogLines);
    }

    @Override
    public void registerOnLogObserver(final ProcessOutputObserver observer) {

	this.outputObservers.add(observer);
    }

    @Override
    public void unRegisterOnLogObserver(final ProcessOutputObserver observer) {

	this.outputObservers.remove(observer);
    }

    @Override
    public void unRegisterAllOnLogObservers() {

	this.outputObservers.clear();
    }

    /*
     * @see de.eqc.srcds.core.ServerOutput#getMaxHistorySize()
     */
    @Override
    public int getMaxHistorySize() {

	return SAVE_LAST_LINES;
    }
}