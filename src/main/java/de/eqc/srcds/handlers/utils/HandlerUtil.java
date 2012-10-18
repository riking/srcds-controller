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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import de.eqc.srcds.handlers.RegisteredHandler;

public final class HandlerUtil {

    /** Hides the constructor of the utility class. */
    private HandlerUtil() {

	throw new UnsupportedOperationException();
    }

    /**
     * This method gets all classes in the same package as
     * RegisterHandlerByReflection which implements the
     * RegisterHandlerByReflection interface.
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    public static Collection<RegisteredHandler> getRegisteredHandlerImplementations()
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	    UnsupportedEncodingException {

	final String pckgname = RegisteredHandler.class.getPackage().getName();
	final Collection<RegisteredHandler> handlers = new ArrayList<RegisteredHandler>();
	Collection<String> classNames = null;
	try {
	    final String path = '/' + pckgname.replace('.', '/');
	    final URL resource = HandlerUtil.class.getResource(path);
	    if (resource == null) {
		throw new ClassNotFoundException("No resource for " + path);
	    }

	    classNames = findHandlerClassNames(resource.getPath().toString(), pckgname);
	} catch (Exception e) {
	    throw new ClassNotFoundException(pckgname + " (" + pckgname
		+ ") does not appear to be a valid package", e);
	}

	for (String className : classNames) {
	    final Class<?> clazz = Class.forName(className);
	    if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())
		&& RegisteredHandler.class.isAssignableFrom(clazz)) {

		handlers.add((RegisteredHandler) clazz.newInstance());
	    }
	}

	return handlers;
    }

    public static Collection<String> findHandlerClassNames(final String path,
							   final String packageName)
	    throws URISyntaxException, IOException {

	final Collection<String> classNames = new ArrayList<String>();
	String ret = packageName;
	if (path.contains(".jar!/")) {
	    ret = ret.replaceAll("\\.", "/");
	    final String parentPath = path.split("!")[0];
	    final FileInputStream fis = new FileInputStream(new File(new URI(parentPath)));
	    final JarInputStream jarFile = new JarInputStream(fis);
	    JarEntry jarEntry;
	    while ((jarEntry = jarFile.getNextJarEntry()) != null) {
		if ((jarEntry.getName().startsWith(ret)) && (jarEntry.getName().endsWith(".class"))) {

		    final String className =
			    jarEntry.getName()
				    .replaceAll("/", "\\.")
				    .substring(0, jarEntry.getName().length() - 6);
		    classNames.add(className);
		}
	    }
	} else {
	    final File directory = new File(path);
	    if (directory.exists()) {
		// Get the list of the files contained in the package
		final String[] files = directory.list();
		for (int i = 0; i < files.length; i++) {
		    // we are only interested in .class files
		    if (files[i].endsWith(".class")) {
			final String className =
				ret + '.' + files[i].substring(0, files[i].length() - 6);
			classNames.add(className);
		    }
		}
	    }
	}

	return classNames;
    }

}