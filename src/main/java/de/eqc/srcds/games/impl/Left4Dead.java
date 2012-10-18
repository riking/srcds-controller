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
package de.eqc.srcds.games.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.eqc.srcds.games.AbstractGame;

public class Left4Dead extends AbstractGame {

    public Left4Dead() {

	super("left4dead");

	addParameter("game", "left4dead");
    }

    @Override
    public List<String> getFilesForEdit() {

	final List<String> files = new ArrayList<String>();
	Collections.addAll(files, // 
			   "left4dead/gameinfo.txt", //
			   "left4dead/missioncycle.txt", // 
			   "left4dead/motd.txt", //
			   "left4dead/cfg/private_server.cfg",// 
			   "left4dead/cfg/server.cfg", //
			   "left4dead/addons/sourcemod/configs/adminmenu_custom.txt",
			   "left4dead/addons/sourcemod/configs/adminmenu_sorting.txt",
			   "left4dead/addons/sourcemod/configs/admins_simple.ini"
			   
	);
	return files;
    }

    @Override
    public List<String> getFilesForSync() {

	final List<String> files = new ArrayList<String>();
	Collections.addAll(files,
			   "left4dead/gameinfo.txt",
			   "left4dead/missioncycle.txt",
			   "left4dead/motd.txt",
			   "left4dead/cfg/server.cfg");
	return files;
    }
}
