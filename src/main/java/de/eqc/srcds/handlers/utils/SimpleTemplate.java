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
package de.eqc.srcds.handlers.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.eqc.srcds.core.Utils;
import de.eqc.srcds.handlers.CssHandler;
import de.eqc.srcds.handlers.ImageHandler;

/**
 * @author Holger Cremer
 */
public final class SimpleTemplate {

    private final Map<String, String> attribute = new HashMap<String, String>();
    private URL template = null;
    private String content = null;

    private final static Pattern IMG_TAG_PATTERN = Pattern.compile("\\$\\{img:([\\w\\.\\-]*)\\}");
    private final static Pattern CSS_TAG_PATTERN = Pattern.compile("\\$\\{css:([\\w\\.\\-]*)\\}");

    /**
     * Creates a simple template from the given template path.
     * 
     * @param templatePath
     * @return
     * @throws FileNotFoundException
     */
    public static SimpleTemplate createTemplateByTemplatePath(final String templatePath) throws FileNotFoundException {

	final SimpleTemplate simpleTemplate = new SimpleTemplate();
	simpleTemplate.template = simpleTemplate.getClass().getResource(templatePath);
	if (simpleTemplate.template == null) {
	    throw new FileNotFoundException(String.format("Cannot find the template '%s'", templatePath));
	}

	return simpleTemplate;
    }

    /**
     * Creates a simple template with the given content.
     * 
     * @param content
     * @return
     */
    public static SimpleTemplate createTemplateFromContent(final String content) {

	final SimpleTemplate simpleTemplate = new SimpleTemplate();
	simpleTemplate.content = content;
	return simpleTemplate;
    }

    // no one must use this constructor
    private SimpleTemplate() {

    }

    public void setAttribute(final String key, final String value) {

	this.attribute.put(key, value);
    }

    public String renderTemplate() throws IOException {

	String templateContent = this.content;

	if (templateContent == null) {
	    templateContent = Utils.getUrlContent(this.template);
	}

	// add the images
	Matcher matcher = IMG_TAG_PATTERN.matcher(templateContent);
	while (matcher.find()) {
	    final MatchResult result = matcher.toMatchResult();
	    final String imageUrl = ImageHandler.HANDLER_PATH + "?name=" + result.group(1);
	    templateContent = templateContent.replace(result.group(0), imageUrl);
	}

	// add the stylesheets
	matcher = CSS_TAG_PATTERN.matcher(templateContent);
	while (matcher.find()) {
	    final MatchResult result = matcher.toMatchResult();
	    final String imageUrl = CssHandler.HANDLER_PATH + "?name=" + result.group(1);
	    templateContent = templateContent.replace(result.group(0), imageUrl);
	}

	// add the attribute into the template
	for (Entry<String, String> entry : this.attribute.entrySet()) {
	    templateContent = templateContent.replace("${" + entry.getKey() + "}", entry.getValue());
	}
	return templateContent;
    }
}
