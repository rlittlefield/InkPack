package com.iopact.InkPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InkPackCommands implements CommandExecutor {
	 private final InkedObjectStorage inkedObjects;
	
	 public InkPackCommands(InkPackPlugin plugin, InkedObjectStorage inkedObjects) {
	        this.inkedObjects = inkedObjects;
	 }
	 
	 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 if (label.equalsIgnoreCase("inkpack")) {
			 return locateInkPack((Player) sender, args);
		 } else if (label.equalsIgnoreCase("applyink")) {
			 return applyInk((Player) sender, args);
		 } else if (label.equalsIgnoreCase("inkpackcreate")) {
			 return createInkPack((Player) sender, args);
		 }
		 return false;
	 }
	 
	 private boolean applyInk(Player sender, String[] args) {
		 ItemStack item = sender.getItemInHand();
		 inkedObjects.inkItemStack(item, sender.getLocation());
		 return true;
	 }
	 
	 private boolean createInkPack(Player sender, String[] args) {
		 ItemStack item = sender.getItemInHand();
		 ItemMeta meta = item.getItemMeta();
		 List<String> lore;
		 if (meta.hasLore()) {
		 	lore = meta.getLore();
		 } else {
 			lore = new ArrayList<String>();
		 }
		 lore.add("InkPack");
		 meta.setLore(lore);
		 item.setItemMeta(meta);
		 sender.sendMessage("Successfully Created InkPack!");
		 return true;
	 }
	 
	 private boolean locateInkPack(Player sender, String[] args) {
		 InkedObject item;
		 if (args.length > 0) {
			 // user is trying to locate a specific item
			 item = inkedObjects.getById(args[0]);
			 InkPackPlugin.getInstance().getLogger().info(args[0]);
			 if (item == null) {
				 return true;
			 }
			 sender.sendMessage("Found inked item: " + item.getLocationDescriptionString());
		 } else {
			 // user wants to see what items were marked in the current chunk
			 Chunk chunk = sender.getLocation().getChunk();
			 HashMap<String, InkedObject> chunkItems = inkedObjects.getByChunk(chunk);
			 for (String key : chunkItems.keySet()) {
				 item = chunkItems.get(key);
				 if (item == null) {
					 continue;
				 }
				 sender.sendMessage("Found inked item: " + item.getLocationDescriptionString());
			 }
		 }
		 return true;
	 }

}
