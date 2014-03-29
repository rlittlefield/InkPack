package com.iopact.InkPack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Chunk;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.logging.Logger;

public class InkedObjectStorage {
	private InkPackPlugin plugin;
	private final Map<String, InkedObject> inked_byid;
	private final Map<String, HashMap<String, InkedObject>> inked_bychunk;
	private static File data;
	private static Logger log;
	private SecureRandom random;
	public InkedObjectStorage(InkPackPlugin plugin, File data) {
		this.plugin = plugin;
		inked_byid = new HashMap<String, InkedObject>();
		inked_bychunk = new HashMap<String, HashMap<String, InkedObject>>();
		this.data = data;
		random = new SecureRandom();
	}
	
	private void load() {
		File inked_objects_file = getInkedObjectsFile();
	}
	
	public void loadInkedObject(String row) {
		int id = 0;
	}
	
	public void save() {
		File inked_objects_file = getInkedObjectsFile();
	}
	
	public boolean alreadyInked(List<String> lore) {
		Boolean alreadyInked = false;
		for (int i = 0; i < lore.size(); i++) {
			if (lore.get(i).startsWith("Inked:")) {
				alreadyInked = true;
				break;
			}
		}
		return alreadyInked;
	}
	
	public String getUnusedSecret() {
		byte bytes[] = new byte[6];
		random.nextBytes(bytes);
		String secret = Hex.encodeHexString(bytes);
		if (inked_byid.containsKey(secret)) {
			return getUnusedSecret();
		}
		return secret;
	}
	
	public InkedObject getById(String id) {
		if (inked_byid.containsKey(id)) {
			return inked_byid.get(id);
		}
		return null;
	}
	
	
	public HashMap<String, InkedObject> getByChunk(Chunk chunk) {
		String chunk_string = chunk.getX()+","+chunk.getZ();
		if (inked_bychunk.containsKey(chunk_string)) {
			return inked_bychunk.get(chunk_string);
		}
		return new HashMap<String, InkedObject>();
	}
	
	/**
	 * @param item
	 * @param loc
	 */
	public void inkItemStack(ItemStack item, Location loc) {
		if (item == null) {
			return;
		}
		ItemMeta meta = item.getItemMeta();
		List<String> lore;
		if (meta.hasLore()) {
			lore = meta.getLore();
		} else {
			lore = new ArrayList<String>();
		}
		if (!alreadyInked(lore)) {
			String secret = getUnusedSecret();
			Chunk chunk = loc.getChunk();
			String location_string = ((int)loc.getX()) + " " + ((int)loc.getY()) + " " + ((int)loc.getZ());
			String chunk_string = chunk.getX()+","+chunk.getZ();
			InkedObject inked = new InkedObject(secret, chunk_string, location_string, location_string, "Unknown", "Unknown");
			inked_byid.put(secret, inked);
			if (item.getType() != Material.INK_SACK) {
				if (inked_bychunk.containsKey(chunk_string)) {
					inked_bychunk.get(chunk_string).put(chunk_string, inked);
				} else {
					HashMap<String, InkedObject> chunk_container = new HashMap<String, InkedObject>();
					chunk_container.put(secret, inked);
					inked_bychunk.put(chunk_string, chunk_container);
				}
			}
			lore.add("Inked:"+secret);
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}
	
	public InkedObject getByItemStack(ItemStack item) {
		if (item == null) {
			return null;
		}
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			return null;
		}
		List<String> lore;
		if (meta.hasLore()) {
			lore = meta.getLore();
		} else {
			return null;
		}
		String inkedId = null;
		for (int i = 0; i < lore.size(); i++) {
			if (lore.get(i).startsWith("Inked:")) {
				String[] inkParts = lore.get(i).split(":");
				if (inkParts.length > 1) {
					inkedId = inkParts[1];
				}
				break;
			}
		}
		if (inkedId != null) {
			return getById(inkedId);
		}
		return null;
	}
	
	private static File getInkedObjectsFile() {
		return new File(data, "inkedobjects.txt");
	}
	
	
}
