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


public final class Constants {

    /** Hides the constructor of the utility class. */
    private Constants() {

	throw new UnsupportedOperationException();
    }
    
    public static final String PROJECT_NAME = "srcds-controller";
    
    public static final String TRAY_ICON_PATH = "/images/icon_tray.gif";
    
    public static final int MILLIS_PER_SEC = 1000;

    public static final int STARTUP_WAIT_TIME_MILLIS = 2000;

    public static final int SHUTDOWN_DELAY_MILLIS = 3000;
    
    public static final int OUTPUT_READING_SHUTDOWN_TIMEOUT_MILLIS = 2000;
    
    public static final int OUTPUT_READING_DELAY_MILLIS = 500;
    
    public static final int HTTP_SERVER_SHUTDOWN_DELAY_SECS = 2;

    public static final int SERVER_POLL_INTERVAL_MILLIS = 1000;

    public static final String FS_LOGGING_FILENAME = "srcds-controller-logging.properties";

    public static final String BUILTIN_LOGGING_FILENAME = "/configuration/srcds-controller-logging.properties";

    public static final String DEFAULT_CONFIG_FILENAME = "srcds-controller-config.xml";
    
}
