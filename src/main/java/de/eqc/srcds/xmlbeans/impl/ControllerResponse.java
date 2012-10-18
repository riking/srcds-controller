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

import de.eqc.srcds.xmlbeans.AbstractXmlBean;
import de.eqc.srcds.xmlbeans.enums.ResponseCode;


public class ControllerResponse extends AbstractXmlBean {

    /**
     * 
     */
    private static final long serialVersionUID = -6455374990668467104L;
    protected ResponseCode code;
    protected Message message;
    
    public ControllerResponse(final ResponseCode code, final Message message) {

	this(code, message, true);
    }

    public ControllerResponse(final ResponseCode code, final Message message, final boolean stylesheet) {

	super(stylesheet);
	this.code = code;
	this.message = message;
    }
    
    @Override
    protected String toXml(final int indent) {

	final StringBuilder sb = new StringBuilder(header(indent));
	sb.append(indent(String.format("<ResponseCode>%s</ResponseCode>\n", code), indent + 1));
	sb.append(message.toXml(indent + 1));
	return sb.append(footer(indent)).toString();
    }

}
