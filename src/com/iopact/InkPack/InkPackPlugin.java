package com.iopact.InkPack;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class InkPackPlugin extends JavaPlugin implements Listener {
    private static InkPackPlugin globalInstance = null;
    private static File data;
    private InkedObjectStorage inkedObjects;
    private static Logger log;
    private InkedObjectManager inkedObjectManager;
	public void onEnable() {
        globalInstance = this;
        File dat = getDataFolder();
		this.data=dat;
		this.inkedObjects = new InkedObjectStorage(this, data);
		log = this.getLogger();
		InkPackCommands commands = new InkPackCommands(this, inkedObjects);
		getCommand("inkpack").setExecutor(commands);
		getCommand("applyink").setExecutor(commands);
		getCommand("inkpackcreate").setExecutor(commands);
		getCommand("inkpackclean").setExecutor(commands);
		log.info("Started InkPack!");
		this.inkedObjectManager = new InkedObjectManager(this, inkedObjects);
		
	}
	
	public static InkPackPlugin getInstance() {
        return globalInstance;
    }
	
	public InkedObjectStorage getInkedObjectManager() {
		return inkedObjects;
	}
	

	
	
}
