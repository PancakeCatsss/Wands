package me.bobcatsss.wands.wands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Turtle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.bobcatsss.wands.Wands;
import me.bobcatsss.wands.utils.ChatUtils;
import me.bobcatsss.wands.wandutils.Wand;
import me.bobcatsss.wands.wandutils.WandKeys;
import me.bobcatsss.wands.wandutils.WandType;

public class BabyWand implements Listener, Wand {
	
	private final Wands plugin = Wands.getInstance();
	
	public BabyWand() {
		Bukkit.getPluginManager().registerEvents(this, Wands.getInstance());
	}


	@Override
	public WandType getType() {
		return WandType.BABY;
	}

	@Override
	public String getName() {
		return "Baby Wand";
	}

	@Override
	public int getCost() {
		return 10000;
	}
	
	public boolean isBabyWand(ItemStack stack) {
		if(stack == null || stack.getType() != Material.STICK || !stack.hasItemMeta()) return false;
		if(!stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING)) return false;
		String type = stack.getItemMeta().getPersistentDataContainer().get(WandKeys.WAND, PersistentDataType.STRING);
		return type.equalsIgnoreCase("BABY");
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = new ItemStack(Material.STICK);
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatUtils.color("&eRight click a mob"));
		lore.add(ChatUtils.color("&eto turn it into a baby"));
		lore.add(ChatUtils.color("&eand locks it's age"));
		meta.setDisplayName(ChatUtils.color("&e&lBaby Wand"));
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(WandKeys.WAND, PersistentDataType.STRING, getType().name());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		return stack;
	}
	
	@EventHandler
	public void onClick(PlayerInteractAtEntityEvent event) {
		Entity e = event.getRightClicked();
		Player player = event.getPlayer();
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if(!isBabyWand(inHand)) return;
		if(event.getHand() != EquipmentSlot.HAND) return;
		if(e == null) return;
		if(!(e instanceof Breedable)) return;
		Breedable a = (Breedable) e;
		if(!plugin.getSkyblockUtils().canUse(event.getPlayer(), e.getLocation())) return;
		if(event.getRightClicked() instanceof Turtle) {
			Turtle turtle = (Turtle) event.getRightClicked();
			turtle.getPersistentDataContainer().set(WandKeys.NODROP, PersistentDataType.STRING, "nodrop");
		}
		if(a.isAdult()) {
		    a.setBaby();
		    a.setAgeLock(true);
		    return;
		}
		a.setAdult();
		a.setAgeLock(false);
	}
	
	@EventHandler
	public void onDrop(EntityDropItemEvent event) {
		if(!(event.getEntity() instanceof Turtle)) return;
		Turtle turtle = (Turtle) event.getEntity();
		if(!turtle.getPersistentDataContainer().has(WandKeys.NODROP, PersistentDataType.STRING)) return;
		event.setCancelled(true);
	}
}
