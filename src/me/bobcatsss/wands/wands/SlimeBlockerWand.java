package me.bobcatsss.wands.wands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

public class SlimeBlockerWand implements Listener, Wand {
	
	private final Wands plugin = Wands.getInstance();
	
	public SlimeBlockerWand() {
		Bukkit.getPluginManager().registerEvents(this, Wands.getInstance());
	}


	@Override
	public WandType getType() {
		return WandType.SLIME_BLOCKER;
	}

	@Override
	public String getName() {
		return "Slime Blocker";
	}

	@Override
	public int getCost() {
		return 10000;
	}
	
	public boolean isWand(ItemStack stack) {
		if(stack == null || stack.getType() != Material.STICK || !stack.hasItemMeta()) return false;
		if(!stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING)) return false;
		String type = stack.getItemMeta().getPersistentDataContainer().get(WandKeys.WAND, PersistentDataType.STRING);
		return type.equalsIgnoreCase("SLIME_BLOCKER");
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = new ItemStack(Material.STICK);
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatUtils.color("&eRight click inside a slime"));
		lore.add(ChatUtils.color("&echunk to disable slimes"));
		lore.add(ChatUtils.color("&espawning in that chunk"));
		meta.setDisplayName(ChatUtils.color("&e&lSlime Blocker Wand"));
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(WandKeys.WAND, PersistentDataType.STRING, getType().name());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		return stack;
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if(event.getEntityType() != EntityType.SLIME) return;
		Chunk chunk = event.getEntity().getLocation().getChunk();
		if(!chunk.isSlimeChunk()) return;
		if(!chunk.getPersistentDataContainer().has(WandKeys.SLIME_BLOCKER, PersistentDataType.STRING)) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) return;
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if(inHand == null) return;
		if(!isWand(inHand)) return;
		if(!plugin.getSkyblockUtils().canUse(player, player.getLocation())) return;
		event.setCancelled(true);
		Chunk chunk = player.getLocation().getChunk();
		if(!chunk.isSlimeChunk()) {
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cThis is not a slime chunk&f."));
			return;
		}
		if(chunk.getPersistentDataContainer().has(WandKeys.SLIME_BLOCKER, PersistentDataType.STRING)) {
			chunk.getPersistentDataContainer().remove(WandKeys.SLIME_BLOCKER);
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &aSlimes can now spawn in this slime chunk&f."));
			return;
		}
		chunk.getPersistentDataContainer().set(WandKeys.SLIME_BLOCKER, PersistentDataType.STRING, "slime_blocker");
		player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cSlimes can no longer spawn in this slime chunk&f."));
		return;
	}
}
