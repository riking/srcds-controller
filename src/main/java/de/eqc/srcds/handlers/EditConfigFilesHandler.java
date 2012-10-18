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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import de.eqc.srcds.configuration.exceptions.ConfigurationException;
import de.eqc.srcds.core.Utils;
import de.eqc.srcds.xmlbeans.impl.GameConfiguration;


/**
 * @author Holger Cremer
 */
public class EditConfigFilesHandler extends AbstractRegisteredHandler {

    /*
     * @see de.eqc.srcds.handlers.AbstractRegisteredHandler#handleRequest(com.sun.net.httpserver.HttpExchange)
     */
    @Override
    public void handleRequest(final HttpExchange httpExchange) throws Exception {
	final List<String> filesForEdit = getServerController().getGameType().getImplementation().getFilesForEdit();

	if (isPost()) {
	    final String fileIdParam = getPostParameter("id");
	    final String newContent = getPostParameter("content");
	    if (fileIdParam == null || newContent == null) {
		throw new IllegalArgumentException("id or content was null");
	    }
	    final int fileId = Integer.parseInt(fileIdParam);
	    saveFile(fileId, filesForEdit.get(fileId), newContent);
	} else {
	    final String fileIdParam = getParameter("id");
	    int fileId = 0;
	    if (fileIdParam != null) {
		fileId = Integer.parseInt(fileIdParam);
	    }
	    showFile(fileId);
	}
    }

    /**
     * @param string
     * @param newContent
     * @throws IOException 
     * @throws ConfigurationException 
     */
    private void saveFile(final int fileId, final String file, final String newContent) throws IOException, ConfigurationException {

	final File fileToEdit = new File(getConfig().getValue("srcds.controller.srcds.path",String.class), file);
	Utils.saveToFile(fileToEdit, newContent);
	showFile(fileId);
    }

    /**
     * @param string
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ConfigurationException 
     */
    private void showFile(final int fileId) throws FileNotFoundException, IOException, ConfigurationException {

	final GameConfiguration gameConfiguration = new GameConfiguration(getConfig(), fileId);
	outputXmlContent(gameConfiguration.toXml());
    }

    /*
     * @see de.eqc.srcds.handlers.RegisteredHandler#getPath()
     */
    @Override
    public String getPath() {
	return "/editConfigFiles";
    }
}