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

import static de.eqc.srcds.core.Constants.SERVER_POLL_INTERVAL_MILLIS;

import java.util.logging.Logger;

import de.eqc.srcds.configuration.Configuration;
import de.eqc.srcds.configuration.exceptions.ConfigurationException;
import de.eqc.srcds.core.logging.LogFactory;
import de.eqc.srcds.enums.ServerState;
import de.eqc.srcds.exceptions.AlreadyRunningException;
import de.eqc.srcds.exceptions.NotRunningException;
import de.eqc.srcds.exceptions.StartupFailedException;


public abstract class AbstractServerController<T> extends Thread {

    private static class Mutex {}

    protected final Configuration config;
    protected final Logger log;
    protected T server;
    protected boolean running;
    private final Mutex mutex;
    private final String subject;
    private boolean autostart;
    
    public AbstractServerController(final String subject, final Configuration config) {

	setName(getClass().getSimpleName());
	this.mutex = new Mutex();
	this.autostart = false;
	this.running = false;
	this.subject = subject;
	this.config = config;
	this.log = LogFactory.getLogger(getClass());
    }
    
    public Mutex getMutex() {

	return mutex;
    }
    
    public void setAutostart(final boolean autostart) {

	this.autostart = autostart;
    }
    
    protected void safeSleep() {

	try {
	    Thread.sleep(SERVER_POLL_INTERVAL_MILLIS);
	} catch (InterruptedException e) {
	    stopGraceful();
	}
    }
    
    public String getSubject() {

	return subject;
    }
    
    public abstract void startServer() throws AlreadyRunningException, StartupFailedException, ConfigurationException;
    public abstract void stopServer() throws NotRunningException;
    public abstract ServerState getServerState();
    
    @Override
    public void run() {

	running = true;
	log.info(String.format("%s controller started", getSubject()));
	
	if (autostart) {
	    try {
		startServer();
		log.info(String.format("%s started", getSubject()));
	    } catch(Exception e) {
		log.info(String.format("%s autostart failed: %s", getSubject(), e.getLocalizedMessage()));
	    }
	}

	try {
	    while (running) {
		safeSleep();
	    }
	} catch (Exception e) {
	    log.warning(String.format("%s controller terminated", getSubject()));
	}
    }
    
    public void stopGraceful() {
	
	try {
	    stopServer();
	    log.info(String.format("%s stopped", getSubject()));
	} catch (Exception e) {
	    log.warning(String.format("Unable to stop %s: %s", getSubject(), e.getLocalizedMessage()));
	}
	
	running = false;
	log.info(String.format("%s controller stopped", getSubject()));
    }    
    
    public boolean isRunning() {

	return running;
    }
    
    
    public T getServer() {

	return server;
    }

}