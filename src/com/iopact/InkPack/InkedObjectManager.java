package com.iopact.InkPack;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class InkedObjectManager implements Listener {
	private final InkedObjectStorage inkedObjects;

	public InkedObjectManager(InkPackPlugin plugin, InkedObjectStorage inkedObjects) {
		this.inkedObjects = inkedObjects;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		// this is a shameful copy paste from the block break event
		InkPackPlugin.getInstance().getLogger().info("Player died!");
		Player player = event.getEntity();
		if (player instanceof InventoryHolder) {
			// if any of the items have a "InkPack" lore, freak out
			InventoryHolder holder = (InventoryHolder) player;
			Inventory inv = holder.getInventory();
			int inklocation = inv.first(Material.INK_SACK);
			if (inklocation == -1) {
				InkPackPlugin.getInstance().getLogger().info("No ink!");
				return;
			}
			ItemStack item = inv.getItem(inklocation);
			ItemMeta meta = item.getItemMeta();
			List<String> lore;
			if (meta.hasLore()) {
				lore = meta.getLore();
			} else {
				InkPackPlugin.getInstance().getLogger().info("Ink has no lore!!");
				return;
			}
			for (String lore_instance : lore) {
				if (lore_instance == "InkPack") {
					ListIterator<ItemStack> it = inv.iterator();
					while (it.hasNext()) {
						InkPackPlugin.getInstance().getLogger().info("Spilling ink everywhere!");
						ItemStack stack = it.next();
						Location loc = player.getLocation();
						inkedObjects.inkItemStack(stack, loc);
					}
					break;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		InkPackPlugin.getInstance().getLogger().info("Block break!");
		Block block = event.getBlock();
		BlockState blockState = block.getState();
		String type_string = block.getType().toString();
		InkPackPlugin.getInstance().getLogger().info("Type: "+type_string);
		if (blockState instanceof InventoryHolder) {
			InkPackPlugin.getInstance().getLogger().info("Its a chest!");
			// if any of the items have a "InkPack" lore, freak out
			InventoryHolder holder = (InventoryHolder) blockState;
			Inventory inv = holder.getInventory();
			int inklocation = inv.first(Material.INK_SACK);
			if (inklocation == -1) {
				InkPackPlugin.getInstance().getLogger().info("No ink!");
				return;
			}
			ItemStack item = inv.getItem(inklocation);
			ItemMeta meta = item.getItemMeta();
			List<String> lore;
			if (meta.hasLore()) {
				lore = meta.getLore();
			} else {
				InkPackPlugin.getInstance().getLogger().info("Ink has no lore!!");
				return;
			}
			for (String lore_instance : lore) {
				if (lore_instance == "InkPack") {
					ListIterator<ItemStack> it = inv.iterator();
					while (it.hasNext()) {
						InkPackPlugin.getInstance().getLogger().info("Spilling ink everywhere!");
						ItemStack stack = it.next();
						Location loc = block.getLocation();
						inkedObjects.inkItemStack(stack, loc);
					}
					break;
				}
			}
		}
	}
	
	// Handle inventory dragging properly.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.isCancelled()) {
			return;
		}
		InkPackPlugin.getInstance().getLogger().info("Inventory drag!");
		Map<Integer, ItemStack> items = event.getNewItems();
		
		for(Integer slot : items.keySet()) {
			ItemStack item = items.get(slot);
			
			InkedObject inked = inkedObjects.getByItemStack(item);
			
			if (inked != null) {
				boolean clickedTop = event.getView().convertSlot(slot) == slot;
				
				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				inked.updateHolder(holder);
				
				if (event.isCancelled()) {
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		InkPackPlugin.getInstance().getLogger().info("Inventory Click!");
		if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR
				|| event.getAction() == InventoryAction.PICKUP_ALL
				|| event.getAction() == InventoryAction.PICKUP_HALF
				|| event.getAction() == InventoryAction.PICKUP_ONE) {
			InkedObject inked = inkedObjects.getByItemStack(event.getCurrentItem());
			
			if (inked != null) {
				inked.updateHolder((Player) event.getWhoClicked());
			}
		} else if (event.getAction() == InventoryAction.PLACE_ALL
				|| event.getAction() == InventoryAction.PLACE_SOME
				|| event.getAction() == InventoryAction.PLACE_ONE) {
			InkedObject inked = inkedObjects.getByItemStack(event.getCursor());
			
			if (inked != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();
				
				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				if (holder != null) {
					inked.updateHolder(holder);
				}
			}
		} else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {			
			InkedObject inked = inkedObjects.getByItemStack(event.getCurrentItem());
			
			if(inked != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();
				
				InventoryHolder holder = !clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				if (holder != null){
					inked.updateHolder(holder);
				}
			}
		} else if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
			PlayerInventory playerInventory = event.getWhoClicked().getInventory();
			InkedObject inked = inkedObjects.getByItemStack(playerInventory.getItem(event.getHotbarButton()));
			
			if (inked != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();

				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				inked.updateHolder(holder);
			}
			
			if (event.isCancelled()) {
				return;
			}
			
			inked = inkedObjects.getByItemStack(event.getCurrentItem());
			
			if (inked != null) {
				inked.updateHolder((Player) event.getWhoClicked());
			}
		} else if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
			InkedObject inked = inkedObjects.getByItemStack(event.getCursor());
			
			if (inked != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();
				
				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				inked.updateHolder(holder);				
			}
			
			if (event.isCancelled()) {
				return;
			}
			
			inked = inkedObjects.getByItemStack(event.getCurrentItem());
			
			if (inked != null) {
				inked.updateHolder((Player) event.getWhoClicked());
			}
		} else if (event.getAction() == InventoryAction.DROP_ALL_CURSOR
				|| event.getAction() == InventoryAction.DROP_ALL_SLOT
				|| event.getAction() == InventoryAction.DROP_ONE_CURSOR
				|| event.getAction() == InventoryAction.DROP_ONE_SLOT) {
			// Handled by onItemSpawn
		} else {
			if (inkedObjects.getByItemStack(event.getCurrentItem()) != null || inkedObjects.getByItemStack(event.getCursor()) != null) {
				((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Error: InkPack doesn't support this inventory functionality quite yet!");
				
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemSpawn(ItemSpawnEvent event) {
		InkPackPlugin.getInstance().getLogger().info("Item Spawn!");
		Item item = event.getEntity();
		ItemStack stack = item.getItemStack();
		InkedObject inked = inkedObjects.getByItemStack(stack);
		
		if (stack.getType() == Material.INK_SACK) {
			ItemMeta meta = stack.getItemMeta();
			List<String> lore;
			if (meta.hasLore()) {
				lore = meta.getLore();
				if (lore != null) {
					for (String lore_row : lore) {
						if (lore_row.startsWith("Inked:")) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		
		if (inked == null) {
			return;
		}
		inked.updateHolder(item);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		InkPackPlugin.getInstance().getLogger().info("Picked UP Item!!");
		ItemStack stack = event.getItem().getItemStack();
		InkedObject inked = inkedObjects.getByItemStack(stack);

		
		if (inked == null) {
			return;
		}
		inked.updateHolder(event.getPlayer());
	}
	

}
