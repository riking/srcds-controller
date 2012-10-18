package de.eqc.srcds.games.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.eqc.srcds.games.AbstractGame;



/**
 * @author Holger Cremer
 */
public class Left4Dead2 extends AbstractGame {

    
    
    /**
     * @param directory
     */
    public Left4Dead2() {

	super("left4dead2");

	addParameter("game", "left4dead2");
    }

    @Override
    public List<String> getFilesForEdit() {

	final List<String> files = new ArrayList<String>();
	Collections.addAll(files, // 
			   "left4dead2/gameinfo.txt", //
			   "left4dead2/missioncycle.txt", // 
			   "left4dead2/motd.txt", //
			   "left4dead2/cfg/private_server.cfg",// 
			   "left4dead2/cfg/server.cfg", //
			   "left4dead2/addons/sourcemod/configs/adminmenu_custom.txt",
			   "left4dead2/addons/sourcemod/configs/adminmenu_sorting.txt",
			   "left4dead2/addons/sourcemod/configs/admins_simple.ini"
			   
	);
	return files;
    }

    @Override
    public List<String> getFilesForSync() {

	return Collections.emptyList();
    }

}
