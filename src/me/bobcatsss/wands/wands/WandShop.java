package me.bobcatsss.wands.wands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.bobcatsss.wands.Wands;
import me.bobcatsss.wands.utils.ChatUtils;
import me.bobcatsss.wands.wandutils.Wand;
import me.bobcatsss.wands.wandutils.WandKeys;
import net.milkbowl.vault.economy.EconomyResponse;

public class WandShop implements Listener, InventoryHolder {
	
	private final Wands plugin = Wands.getInstance();
	private final Inventory inventory;
	private final NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
	
	public WandShop() {
		this.inventory = Bukkit.createInventory(this, 27, ChatUtils.color("&cWand Shop"));
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, getFiller());
		}
		if(plugin.getWand("Sell") != null) {
			inventory.setItem(10, getSellItem(plugin.getWand("Sell")));
		}
		if(plugin.getWand("ENTITY_PICKUP") != null) {
			inventory.setItem(11, getSellItem(plugin.getWand("ENTITY_PICKUP")));
		}
		if(plugin.getWand("BABY") != null) {
			inventory.setItem(12, getSellItem(plugin.getWand("BABY")));
		}
		if(plugin.getWand("SLIME_CHUNK") != null) {
			inventory.setItem(13, getSellItem(plugin.getWand("SLIME_CHUNK")));
		}
		if(plugin.getWand("SLIME_BLOCKER") != null) {
			inventory.setItem(14, getSellItem(plugin.getWand("SLIME_BLOCKER")));
		}
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public ItemStack getSellItem(Wand wand) {
		ItemStack stack = wand.getItem().clone();
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(meta.hasLore()) lore = meta.getLore();
		lore.add(ChatUtils.color("&fPrice: &a$" + nf.format(wand.getCost())));
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public boolean isWand(ItemStack stack) {
		if(stack == null || stack.getType() != Material.STICK || !stack.hasItemMeta()) return false;
		return stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING);
	}
	
	public String getType(ItemStack stack) {
		if(stack == null || stack.getType() != Material.STICK || !stack.hasItemMeta()) return null;
		if(!stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING)) return null;
		return stack.getItemMeta().getPersistentDataContainer().get(WandKeys.WAND, PersistentDataType.STRING);
	}
	
	public ItemStack getFiller() {
		ItemStack stack = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatUtils.color("&a"));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(event.getInventory() == null) return;
		ItemStack stack = event.getCurrentItem();
		if(!(event.getInventory().getHolder() instanceof WandShop)) return;
		if(stack == null) return;
		if(!isWand(stack) || getType(stack) == null) {
			event.setCancelled(true);
			event.setResult(Result.DENY);
			return;
		}
		event.setCancelled(true);
		event.setResult(Result.DENY);
		Wand wand = plugin.getWand(getType(stack));
		if(player.getInventory().firstEmpty() == -1) {
			player.closeInventory();
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cYour inventory is full"));
			return;
		}
		EconomyResponse r = plugin.getEco().withdrawPlayer(player, wand.getCost());
		if(r.transactionSuccess()) {
			player.closeInventory();
			player.getInventory().addItem(wand.getItem());
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &aSuccessfully purchased the &f" + wand.getName() + " &awand"));
		} else {
			player.closeInventory();
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cYou don't have enough money to buy this wand"));
		}
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(event.getInventory() == null) return;
		if(!(event.getInventory().getHolder() instanceof WandShop)) return;
		event.setCancelled(true);
		event.setResult(Result.DENY);
	}
}
