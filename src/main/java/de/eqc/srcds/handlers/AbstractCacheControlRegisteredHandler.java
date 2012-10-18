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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import de.eqc.srcds.core.Utils;
import de.eqc.srcds.core.logging.LogFactory;

/**
 * @author Holger Cremer
 */
public abstract class AbstractCacheControlRegisteredHandler extends AbstractRegisteredHandler {

    /**
     * This is the date format in a http reponse.
     */
    public static final String HTTP_RESPONSE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    private static Logger log = LogFactory.getLogger(AbstractCacheControlRegisteredHandler.class);

    /**
     * 86400 seconds = one day
     */
    private static final long EXPIRES_IN = 86400000;

    /*
     * @see
     * de.eqc.srcds.handlers.AbstractRegisteredHandler#handle(com.sun.net.httpserver
     * .HttpExchange)
     */
    @Override
    public void handle(final HttpExchange httpExchange) throws IOException {

	final long lastModified = (Utils.getLastModifiedDate() / 1000) * 1000;

	final SimpleDateFormat dateFormat = new SimpleDateFormat(HTTP_RESPONSE_DATE_FORMAT, Locale.ENGLISH);

	// send cache control headers
	httpExchange.getResponseHeaders().add("Cache-Control", "public, max-age=" + EXPIRES_IN + ", must-revalidate");
	httpExchange.getResponseHeaders().add("Expires", dateFormat.format(new Date(System.currentTimeMillis() + EXPIRES_IN)));
	httpExchange.getResponseHeaders().add("Last-Modified", dateFormat.format(new Date(lastModified)));

	// looking for "If-modified-since" to send a 304 status
	for (Entry<String, List<String>> entry : httpExchange.getRequestHeaders().entrySet()) {
	    if (entry.getKey().equalsIgnoreCase("If-modified-since")) {
		final Date parsedDate = parseHeaderDate(entry.getValue(), dateFormat);
		if (parsedDate != null && lastModified <= parsedDate.getTime()) {
		    // skip the request and send a http 304
		    httpExchange.sendResponseHeaders(304, 0);
		    final OutputStream os = httpExchange.getResponseBody();
		    os.flush();
		    os.close();
		    return;
		}
	    }
	}

	// handle the request normal
	super.handle(httpExchange);
    }

    private Date parseHeaderDate(final List<String> valueList, final SimpleDateFormat dateFormat) {

	Date ret = null;
	if (valueList.size() != 1) {
	    log.warning("Expected size of values is 1 but was: " + valueList.size());
	} else {
	    try {
		ret = dateFormat.parse(valueList.get(0));
	    } catch (ParseException excp) {
		log.log(Level.WARNING,
			"Can't parse the if-modified-since date '" + valueList.get(0) + "': " + excp.getMessage(),
			excp);
	    }
	}
	return ret;
    }
}
