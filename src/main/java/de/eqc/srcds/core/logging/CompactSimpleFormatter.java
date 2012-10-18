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
package de.eqc.srcds.core.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;


/**
 * Mostly like the {@link SimpleFormatter} but outputs only one line per log record.
 * 
 * @author Holger Cremer
 */
public class CompactSimpleFormatter extends Formatter {

    private final Date dat = new Date();
    private final static String FORMAT_PATTERN = "{0,date} {0,time}";
    private MessageFormat formatter;

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private final String lineSeparator = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));

    private String getClassNameWithoutPackage(final String sourceClassName) {
	final int lastIndexOf = sourceClassName.lastIndexOf('.');
	String ret;
	if (lastIndexOf == -1) {
	    ret = sourceClassName;
	} else {
	    ret = sourceClassName.substring(lastIndexOf + 1);
	}
	return ret; 
    }
    
    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(final LogRecord record) {
	final StringBuilder sb = new StringBuilder();
	// Minimize memory allocations here.
	dat.setTime(record.getMillis());
	Object args[] = new Object[1];
	args[0] = dat;
	final StringBuffer text = new StringBuffer();
	if (formatter == null) {
	    formatter = new MessageFormat(FORMAT_PATTERN);
	}
	formatter.format(args, text, null);
	sb.append(text);
	sb.append(" [");
	if (record.getSourceClassName() != null) {	
	    sb.append(getClassNameWithoutPackage(record.getSourceClassName()));
	} else {
	    sb.append(record.getLoggerName());
	}
	sb.append("] ");

	final String message = formatMessage(record);
	sb.append(record.getLevel().getName());
	sb.append(": ");
	sb.append(message);
	sb.append(lineSeparator);
	if (record.getThrown() != null) {
	    try {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
	        record.getThrown().printStackTrace(pw);
	        pw.close();
		sb.append(sw.toString());
	    } catch (Exception ex) {
	    }
	}
	return sb.toString();
    }
}
