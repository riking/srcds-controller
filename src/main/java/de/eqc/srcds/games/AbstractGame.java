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
package de.eqc.srcds.games;

import java.util.AbstractSequentialList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.eqc.srcds.enums.OperatingSystem;

public abstract class AbstractGame {

    private final Map<String, String> parameters;
    private final String directory;

    public AbstractGame(final String directory) {

	this.directory = directory;
	this.parameters = new HashMap<String, String>();
    }

    public Map<String, String> getParameters() {

	return parameters;
    }

    protected void addParameter(final String key, final String value) {

	if (parameters.get(key) == null) {
	    parameters.put(key, value);
	}
    }

    public AbstractSequentialList<String> getParametersAsList() {

	final LinkedList<String> params = new LinkedList<String>();

	for (String parameter : parameters.keySet()) {
	    if (OperatingSystem.getCurrent() == OperatingSystem.LINUX) {
		params.add("-" + parameter + " " + parameters.get(parameter));
	    } else {
		params.add("-" + parameter);
		params.add(parameters.get(parameter));
	    }
	}

	return params;
    }

    public String getDirectory() {

	return directory;
    }

    public abstract List<String> getFilesForEdit();

    public abstract List<String> getFilesForSync();
}
