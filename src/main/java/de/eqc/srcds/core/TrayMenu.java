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
package de.eqc.srcds.core;

import static de.eqc.srcds.core.Constants.PROJECT_NAME;
import static de.eqc.srcds.core.Constants.TRAY_ICON_PATH;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import de.eqc.srcds.configuration.Configuration;
import de.eqc.srcds.exceptions.UnsupportedOSException;

public class TrayMenu {

    private final TrayIcon trayIcon;

    public TrayMenu(final Configuration config) throws UnsupportedOSException {

	if (SystemTray.isSupported()) {
	    final SystemTray tray = SystemTray.getSystemTray();
	    final URL iconUrl = getClass().getResource(TRAY_ICON_PATH);
	    final Image image = Toolkit.getDefaultToolkit().getImage(iconUrl);

	    final ActionListener exitListener = new ActionListener() {
		public void actionPerformed(final ActionEvent event) {
		    System.exit(0);
		}
	    };

	    final PopupMenu popup = new PopupMenu();

	    final MenuItem webConsoleItem = new MenuItem("Web Console");
	    popup.add(webConsoleItem);

	    final MenuItem aboutItem = new MenuItem("About");
	    popup.add(aboutItem);

	    final MenuItem exitItem = new MenuItem("Exit");
	    exitItem.addActionListener(exitListener);
	    popup.add(exitItem);

	    final ActionListener actionListener = new ActionListener() {

		public void actionPerformed(final ActionEvent event) {

		    if (event.getSource().equals(aboutItem)) {
			getTrayIcon().displayMessage("About", String.format(
				"%s v%s", PROJECT_NAME, VersionUtil
					.getProjectVersion()),
				TrayIcon.MessageType.INFO);
		    } else if (event.getSource().equals(webConsoleItem)) {
			try {
			    String command = String.format(
				    "rundll32 url.dll,FileProtocolHandler %s",
				    NetworkUtil.getHomeUrl(config));
			    Runtime.getRuntime().exec(command);
			} catch (Exception ex) {
			    // Ignore
			}
		    }
		}
	    };

	    trayIcon = new TrayIcon(image, PROJECT_NAME, popup);
	    trayIcon.setImageAutoSize(true);
	    trayIcon.addActionListener(actionListener);

	    webConsoleItem.addActionListener(actionListener);
	    aboutItem.addActionListener(actionListener);

	    try {
		tray.add(trayIcon);
	    } catch (AWTException e) {
		throw new IllegalStateException("Unable to add icon to tray");
	    }
	} else {

	    throw new UnsupportedOSException(
		    "System does not support tray icons");
	}
    }
    
    protected TrayIcon getTrayIcon() {

	return this.trayIcon;
    }
}
