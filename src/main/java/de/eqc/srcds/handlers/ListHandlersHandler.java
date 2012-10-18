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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import de.eqc.srcds.handlers.utils.HandlerUtil;
import de.eqc.srcds.xmlbeans.enums.ResponseCode;
import de.eqc.srcds.xmlbeans.impl.ControllerResponse;
import de.eqc.srcds.xmlbeans.impl.Message;

public class ListHandlersHandler extends AbstractRegisteredHandler
	implements RegisteredHandler {

    @Override
    public String getPath() {
	return "/usage";
    }

    public void handleRequest(final HttpExchange httpExchange) throws IOException {
	
	try {
	    final Collection<RegisteredHandler> handlers = HandlerUtil.getRegisteredHandlerImplementations();
	    final List<String> lines = new LinkedList<String>();
	    for (RegisteredHandler handler : handlers) {
		lines.add(handler.getPath());
	    }
	    final String[] linesAsArray = lines.toArray(new String[0]); 
	    final ControllerResponse cr = new ControllerResponse(ResponseCode.INFORMATION, new Message(linesAsArray));
	    outputXmlContent(cr.toXml());
	} catch (Exception e) {
	    throw new IOException(String.format("Unable to register handler %s", getClass()), e);
	}
	
    }
}