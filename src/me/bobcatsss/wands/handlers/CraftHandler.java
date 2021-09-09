package me.bobcatsss.wands.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.bobcatsss.wands.wandutils.WandKeys;

public class CraftHandler implements Listener {
	
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		for(ItemStack stack : event.getInventory().getContents()) {
			if(stack == null) continue;
			if(!stack.hasItemMeta()) continue;
			if(!stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING)) continue;
			event.setCancelled(true);
			return;
		}
	}
}
