package me.bobcatsss.wands.wands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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

public class EntityPickupWand implements Listener, Wand {
	
	private final Wands plugin = Wands.getInstance();
	
	public EntityPickupWand() {
		Bukkit.getPluginManager().registerEvents(this, Wands.getInstance());
	}

	@Override
	public WandType getType() {
		return WandType.ENTITY_PICKUP;
	}

	@Override
	public String getName() {
		return "Entity Mover";
	}

	@Override
	public int getCost() {
		return 10000;
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = new ItemStack(Material.STICK);
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatUtils.color("&eRight click a mob to pick it up"));
		lore.add(ChatUtils.color("&eShift to put it down"));
		meta.setDisplayName(ChatUtils.color("&e&lEntity Mover"));
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(WandKeys.WAND, PersistentDataType.STRING, getType().name());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		return stack;
	}
	
	public boolean isWand(ItemStack stack) {
		if(stack == null || stack.getType() != Material.STICK || !stack.hasItemMeta()) return false;
		if(!stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING)) return false;
		String type = stack.getItemMeta().getPersistentDataContainer().get(WandKeys.WAND, PersistentDataType.STRING);
		return type.equalsIgnoreCase("ENTITY_PICKUP");
	}
	
	public void handle(Player player, Entity entity) {
		if(Wands.getInstance().getEco() == null) {
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cSomething went wrong and to protect your items you cannot sell at this time&f."));
			return;
		}
		if(!(entity instanceof LivingEntity)) return;
	}
	
	@EventHandler
	public void onClick(PlayerInteractAtEntityEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) return;
		Entity e = event.getRightClicked();
		Player player = event.getPlayer();
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if(inHand == null) return;
		if(!isWand(inHand)) return;
		if(e == null) return;
		if(!(e instanceof LivingEntity)) return;
		if(!plugin.getSkyblockUtils().canUse(player, e.getLocation())) return;
		event.setCancelled(true);
		player.addPassenger(e);
	}
	
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		if(!event.isSneaking()) return;
		Player player = event.getPlayer();
		player.getPassengers().forEach(en -> en.getVehicle().eject());
	}
}
