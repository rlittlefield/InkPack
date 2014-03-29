package com.iopact.InkPack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Item;


import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Chunk;

public class InkedObject {
	private String id;
	private String startChunk;
	private String startLocation;
	private String currentLocation;
	private String holder;
	private String holderType;
	
	public InkedObject(String id, String startChunk, String startLocation, String currentLocation, String holder, String holderType) {
		this.id = id;
		this.startChunk = startChunk;
		this.startLocation = startLocation;
		this.currentLocation = currentLocation;
		this.holder = holder;
		this.holderType = "";
	}
	

	public String getId() {
		return id;
	}
	
	public String getLocationDescriptionString() {
		String typePrefix = "";
		if (holderType == "player") {
			typePrefix = "Player:";
			Player holderPlayer = Bukkit.getPlayer(holder);
			if (holderPlayer != null) {
				Location loc = holderPlayer.getLocation();
				currentLocation = ((int)loc.getX()) + " " + ((int)loc.getY()) + " " + ((int)loc.getZ());;
			}
		}
		return id + ", [" + currentLocation + "] " + typePrefix + holder; 
	}
	
	
	
	public void updateHolder(InventoryHolder current_holder) {
		holderType = "block";
		if (current_holder instanceof Chest) {
			holder = "Chest";
		} else if (current_holder instanceof DoubleChest) {
			holder = "Chest";
		} else if (current_holder instanceof Furnace) {
			holder = "Furnace";
		} else if (current_holder instanceof Dispenser) {
			holder = "Dispenser";
		} else if (current_holder instanceof BrewingStand) {
			holder = "Brewing Stand";
		} else if (current_holder instanceof Player) {
			Player player = (Player) current_holder;
			holderType = "player";
			holder = player.getDisplayName();
		} else {
			return;
		}
		
		Location loc = null;
		if (current_holder instanceof Entity) {
			Entity ent = (Entity) current_holder;
			loc = ent.getLocation();
		} else if (current_holder instanceof Block) {
			Block block = (Block) current_holder;
			loc = block.getLocation();
		}

		if (loc != null) {
			if (startChunk == "" || startChunk == null) {
				Chunk chunk = loc.getChunk();
				startChunk = ((int)chunk.getX()) +","+((int)chunk.getZ());
			}
			currentLocation = ((int)loc.getX()) + " " + ((int)loc.getY()) + " " + ((int)loc.getZ());
			if (startLocation == "" || startLocation == null) {
				startLocation = currentLocation;
			}
		}

	}
	
	public void updateHolder(Item item) {
		InkPackPlugin.getInstance().getLogger().info("updating ground location");
		Location loc = item.getLocation();
		currentLocation = ((int)loc.getX()) + " " + ((int)loc.getY()) + " " + ((int)loc.getZ());
		holder = "Ground";
		holderType = "ground";

	}
	
	
	
	
}
