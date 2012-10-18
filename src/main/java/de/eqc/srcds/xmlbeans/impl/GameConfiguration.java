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
package de.eqc.srcds.xmlbeans.impl;

import static de.eqc.srcds.configuration.ConfigurationRegistry.SRCDS_GAMETYPE;

import java.io.File;

import de.eqc.srcds.configuration.Configuration;
import de.eqc.srcds.core.Utils;
import de.eqc.srcds.enums.GameType;
import de.eqc.srcds.games.AbstractGame;
import de.eqc.srcds.xmlbeans.AbstractXmlBean;

public class GameConfiguration extends AbstractXmlBean {

    /**
     * 
     */
    private static final long serialVersionUID = -7008363335234493278L;
    private final Configuration config;
    private final int selectedFileIndex;

    public GameConfiguration(final Configuration config, final int selectedFileIndex) {

	super(true);
	this.config = config;
	this.selectedFileIndex = selectedFileIndex;
    }

    @Override
    protected String toXml(final int indent) {

	final StringBuilder sb = new StringBuilder(header(indent));
	try {
	    final AbstractGame game = config.getValue(SRCDS_GAMETYPE, GameType.class).getImplementation();

	    sb.append(indent("<ConfigurationFiles>\n", indent + 1));
	    for (int id = 0; id < game.getFilesForEdit().size(); id++) {
		final String configFile = game.getFilesForEdit().get(id);
		sb.append(indent(String.format("<ConfigurationFile id=\"%d\" name=\"%s\" />\n", id, configFile), indent + 2));
	    }
	    sb.append(indent("</ConfigurationFiles>\n", indent + 1));

	    final File file =
		    new File(config.getValue("srcds.controller.srcds.path", String.class), game.getFilesForEdit()
											       .get(selectedFileIndex));

	    sb.append(indent(String.format("<FileContent id=\"%d\" fileExists=\"%b\" folderExists=\"%b\">",
					   selectedFileIndex,
					   file.exists(),
					   file.getParentFile().exists()), indent + 1));
	    sb.append("<![CDATA[");
	    if (file.exists()) {
		sb.append(Utils.getFileContent(file));
	    }
	    sb.append("]]>");
	    sb.append("</FileContent>\n");
	} catch (Exception e) {
	    sb.append("<Error>Unable to load required resources</Error>");
	}

	return sb.append(footer(indent)).toString();
    }
}