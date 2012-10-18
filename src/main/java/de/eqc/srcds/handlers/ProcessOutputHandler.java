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
package de.eqc.srcds.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.Channels;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.eqc.srcds.configuration.Configuration;
import de.eqc.srcds.core.ProcessOutputObserver;
import de.eqc.srcds.core.ServerOutput;
import de.eqc.srcds.core.SourceDServerController;
import de.eqc.srcds.core.Utils;
import de.eqc.srcds.core.logging.LogFactory;
import de.eqc.srcds.enums.ServerState;

/**
 * @author Holger Cremer
 */
public class ProcessOutputHandler extends AbstractRegisteredHandler  {

    private static Logger log = LogFactory.getLogger(ProcessOutputHandler.class);
    private SourceDServerController serverController;

    /*
     * @see de.eqc.srcds.handlers.RegisteredHandler#getPath()
     */
    @Override
    public String getPath() {

	return "/processOutput";
    }

    /*
     * @see de.eqc.srcds.handlers.RegisteredHandler#getHttpHandler()
     */
    @Override
    public HttpHandler getHttpHandler() {

	return this;
    }

    /*
     * @seede.eqc.srcds.handlers.RegisteredHandler#init(de.eqc.srcds.core.
     * SourceDServerController, de.eqc.srcds.configuration.Configuration)
     */
    @Override
    public void init(final SourceDServerController controller, final Configuration config) {

	this.serverController = controller;
    }

    /*
     * @see
     * de.eqc.srcds.handlers.AbstractRegisteredHandler#handleRequest(com.sun
     * .net.httpserver.HttpExchange)
     */
    @Override
    public void handleRequest(final HttpExchange httpExchange) throws Exception {

	httpExchange.getResponseHeaders().add("Content-type", "text/html");
	httpExchange.sendResponseHeaders(200, 0);
	PrintStream printStream = null;

	final ServerOutput serverOutput = this.serverController.getServerOutput();
	    final OutputStream os = httpExchange.getResponseBody();

	    // set autoflush on in constructor
	    printStream = new PrintStream(os, true);
	    printStream.write(getHtmlHeader().getBytes());

	    // output the log history
	    printStream.println("<h2>Output History</h2>");
	    if (serverOutput == null) {
		printStream.println("<b>SRCDS Server is not running</b>");
		Utils.closeQuietly(printStream);
		return;
	    } else {
		for (String logLine : serverOutput.getLastLog()) {
		    printStream.println(logLine + "<br/>");
		}

		if (this.serverController.getServerState() != ServerState.RUNNING) {
		    printStream.println("<b>SRCDS Server stopped</b>");
		    Utils.closeQuietly(printStream);
		} else {
		    printStream.println("<h2>Live Output</h2>");
		    Channels.newChannel(printStream);
		    final WriteLogToStreamThread streamLogger =
			    new WriteLogToStreamThread(printStream, serverOutput);
		    streamLogger.start();
		}
	    }
    }

    /**
     * Monitors the output stream and prints new process output lines on it.
     * 
     * @author Holger Cremer
     */
    class WriteLogToStreamThread extends Thread implements ProcessOutputObserver {

	private final PrintStream printStream;
	private final ServerOutput serverOutput;

	/**
	 * @param printStream
	 * @param serverOutput
	 */
	public WriteLogToStreamThread(final PrintStream printStream, final ServerOutput serverOutput) {

	    this.setName("WriteLogToStreamThread");
	    this.printStream = printStream;
	    this.serverOutput = serverOutput;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

	    serverOutput.registerOnLogObserver(this);
	    try {
		/*
		 * If the browser disconnects 'checkError()' returns true and we
		 * stop this thread. (PrintStream omit any IOExceptions !)
		 */
		while (!this.printStream.checkError()) {
		    try {
			// sleeping 1 sec. seem to be a good compromise between
			// cpu usage and actuality
			Thread.sleep(1000);
		    } catch (InterruptedException excp) {
			// ignore
		    }
		}
	    } finally {
		serverOutput.unRegisterOnLogObserver(this);
		try {
		    this.printStream.write(getHtmlFooter().getBytes());
		} catch (IOException e) {
		    // Ignore
		} finally {
			Utils.closeQuietly(this.printStream);
		}
	    }
	}

	/*
	 * @see
	 * de.eqc.srcds.core.ProcessOutputObserver#outputHasChanged(java.lang
	 * .String)
	 */
	@Override
	public void outputHasChanged(final String newLine) {

	    this.printStream.println(newLine + "<br />");
	}
    }

}
