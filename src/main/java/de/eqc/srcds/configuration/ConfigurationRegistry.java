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
package de.eqc.srcds.configuration;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.eqc.srcds.configuration.datatypes.Password;
import de.eqc.srcds.enums.GameType;

public final class ConfigurationRegistry {

    /** Hides the constructor of the utility class. */
    private ConfigurationRegistry() {

	throw new UnsupportedOperationException();
    }

    public static final List<String> FORBIDDEN_USER_PARAMETERS;
    static {
	FORBIDDEN_USER_PARAMETERS = Arrays.asList("-game", "-console", "+hostport");
    }
    
    public static final String HTTP_SERVER_PORT = "srcds.controller.networking.httpserver.port";

    public static final String HTTP_SERVER_USERNAME = "srcds.controller.networking.httpserver.username";

    public static final String HTTP_SERVER_PASSWORD = "srcds.controller.networking.httpserver.password";

    public static final String SYNC_URL = "srcds.controller.sync.url";

    public static final String SYNC_USERNAME = "srcds.controller.srcds.sync.username";

    public static final String SYNC_PASSWORD = "srcds.controller.srcds.sync.password";

    public static final String AUTOSTART = "srcds.controller.srcds.autostart";

    public static final String SRCDS_PATH = "srcds.controller.srcds.path";

    public static final String SRCDS_EXECUTABLE = "srcds.controller.srcds.executable";

    public static final String SRCDS_PARAMETERS = "srcds.controller.srcds.parameters";

    public static final String SRCDS_GAMETYPE = "srcds.controller.srcds.gametype";

    public static final String SRCDS_SERVER_PORT = "srcds.controller.srcds.server.port";

    public static final String SRCDS_RCON_PASSWORD = "srcds.controller.srcds.rcon.password";

    private static List<ConfigurationKey<?>> entries;
    static {
	entries = new LinkedList<ConfigurationKey<?>>();
	entries.add(new ConfigurationKey<Integer>(HTTP_SERVER_PORT, 8888,
		"HTTP Server Port", 0));
	entries.add(new ConfigurationKey<String>(HTTP_SERVER_USERNAME, "admin",
		"HTTP Server Username", 1));
	entries.add(new ConfigurationKey<Password>(HTTP_SERVER_PASSWORD,
		new Password("joshua"), "HTTP Server Password", 2));
	entries.add(new ConfigurationKey<String>(SYNC_URL, "http://",
		"Synchronization URL", 3));
	entries.add(new ConfigurationKey<String>(SYNC_USERNAME, "admin",
		"Synchronization Username", 4));
	entries.add(new ConfigurationKey<Password>(SYNC_PASSWORD, new Password(
		"joshua"), "Synchronization Password", 5));
	entries.add(new ConfigurationKey<Boolean>(AUTOSTART, false,
		"SRCDS Autostart", 6));
	entries.add(new ConfigurationKey<GameType>(SRCDS_GAMETYPE,
		GameType.LEFT4DEAD, "SRCDS Game Type", 7));
	entries.add(new ConfigurationKey<String>(SRCDS_PATH, "./",
		"SRCDS Path", 8));
	entries.add(new ConfigurationKey<String>(SRCDS_EXECUTABLE, "srcds_i486",
		"SRCDS Executable", 9));
	entries.add(new ConfigurationKey<String>(SRCDS_PARAMETERS, "",
		"SRCDS Parameters", 10));
	entries.add(new ConfigurationKey<Integer>(SRCDS_SERVER_PORT, 27015,
		"SRCDS Server Port", 11));
	entries.add(new ConfigurationKey<Password>(SRCDS_RCON_PASSWORD,
		new Password("joshua"), "RCON Password", 12));
    }

    public static ConfigurationKey<?> getEntryByKey(final String key) {

	ConfigurationKey<?> match = null;
	for (ConfigurationKey<?> entry : entries) {
	    if (entry.getKey().equals(key)) {
		match = entry;
		break;
	    }
	}
	return match;
    }

    public static List<ConfigurationKey<?>> getEntries() {

	return entries;
    }

    public static boolean matchesDataType(final String key, final Class<?> dataType) {
	
	return getEntryByKey(key).getDataType() == dataType;
    }

}