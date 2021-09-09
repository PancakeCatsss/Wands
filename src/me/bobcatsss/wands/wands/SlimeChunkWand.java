package me.bobcatsss.wands.wands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.bobcatsss.wands.Wands;
import me.bobcatsss.wands.utils.ChatUtils;
import me.bobcatsss.wands.wandutils.Wand;
import me.bobcatsss.wands.wandutils.WandKeys;
import me.bobcatsss.wands.wandutils.WandType;

public class SlimeChunkWand implements Listener, Wand {

	private final Wands plugin = Wands.getInstance();
	
	public SlimeChunkWand() {
		Bukkit.getPluginManager().registerEvents(this, Wands.getInstance());
		new BukkitRunnable() {
			@Override
			public void run() {
				checkLoadedChunks();
			}
		}.runTaskLater(plugin, 200);
	}

	@Override
	public WandType getType() {
		return WandType.SLIME_CHUNK;
	}

	@Override
	public String getName() {
		return "Slime Chunk Finder";
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
		lore.add(ChatUtils.color("&3Right click to highlight any"));
		lore.add(ChatUtils.color("&3Slime Chunks within 32 x 32 blocks"));
		lore.add(ChatUtils.color("&c2 minute cooldown"));
		meta.setDisplayName(ChatUtils.color("&e&lSlime Chunk Finder"));
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
		return type.equalsIgnoreCase("SLIME_CHUNK");
	}
	
	private List<Chunk> getSlimeChunks(Chunk origin, int radius) {
	    World world = origin.getWorld();
	    List<Chunk> chunks = new ArrayList<>();
	    int cX = origin.getX();
	    int cZ = origin.getZ();
	    for (int x = -radius; x <= radius; x++) {
	        for (int z = -radius; z <= radius; z++) {
	            if(world.getChunkAt(cX + x, cZ + z).isSlimeChunk()) {
	            	chunks.add(world.getChunkAt(cX + x, cZ + z));
	            }
	        }
	    }
	    return chunks;
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
		if(inHand.getItemMeta().getPersistentDataContainer().has(WandKeys.COOLDOWN, PersistentDataType.LONG)) {
			long time = inHand.getItemMeta().getPersistentDataContainer().get(WandKeys.COOLDOWN, PersistentDataType.LONG);
			if(time > System.currentTimeMillis()) {
				player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cCurrently still on a cooldown&f."));
				return;
			}
		}
		List<Chunk> chunks = getSlimeChunks(player.getLocation().getChunk(), 2);
		if(chunks.isEmpty()) {
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cNo slime chunks found&f."));
			ItemMeta meta = inHand.getItemMeta();
			meta.getPersistentDataContainer().set(WandKeys.COOLDOWN, PersistentDataType.LONG, System.currentTimeMillis() + 120000);
			inHand.setItemMeta(meta);
			return;
		}
		int y = player.getLocation().getBlockY();
		for(Chunk chunk : chunks) {
			Location a = new Location(chunk.getWorld(), chunk.getX()*16, y, chunk.getZ()*16);
			Location b = new Location(chunk.getWorld(), chunk.getX()*16+15, y, chunk.getZ()*16);
			Location c = new Location(chunk.getWorld(), chunk.getX()*16, y, chunk.getZ()*16+15);
			Location d = new Location(chunk.getWorld(), chunk.getX()*16+15, y, chunk.getZ()*16+15);
			spawnShulker(Arrays.asList(a, b, c, d));
		}
		ItemMeta meta = inHand.getItemMeta();
		meta.getPersistentDataContainer().set(WandKeys.COOLDOWN, PersistentDataType.LONG, System.currentTimeMillis() + 120000);
		inHand.setItemMeta(meta);
		player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &aSlime Chunk(s) have been found&f, &athey are marked by glowing blocks that will expire after 30 seconds&f."));
	}
	
	private void spawnShulker(List<Location> locations) {
		List<Shulker> shulkers = new ArrayList<>();
		for(Location loc : locations) {
			Shulker s = (Shulker) loc.getWorld().spawnEntity(loc, EntityType.SHULKER);
			s.setAI(false);
			s.setGravity(false);
			s.setGlowing(true);
			s.setInvisible(true);
			s.setInvulnerable(true);
			s.getPersistentDataContainer().set(WandKeys.SHULKER, PersistentDataType.STRING, "marker");
			shulkers.add(s);
		}
		removeShulkers(shulkers);
	}
	
	private void removeShulkers(List<Shulker> shulkers) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Shulker s : shulkers) {
					if(s == null || s.isDead()) {
						continue;
					}
					s.remove();
				}
			}
		}.runTaskLater(plugin, 600);
	}
	
	public void checkLoadedChunks() {
		World world = Bukkit.getWorld("bskyblock_world");
		if(world == null) return;
		if(world.getLoadedChunks().length == 0) return;
		for(Chunk chunk : world.getLoadedChunks()) {
			if(chunk.getEntities().length == 0) continue;
			for(Entity e : chunk.getEntities()) {
				if(!(e instanceof Shulker)) continue;
				Shulker s = (Shulker)e;
				if(!s.getPersistentDataContainer().has(WandKeys.SHULKER, PersistentDataType.STRING)) continue;
				s.remove();
			}
		}
	}
	
	@EventHandler
	public void unLoad(ChunkUnloadEvent event) {
		if(event.getChunk().getEntities().length == 0) return;
		for(Entity e : event.getChunk().getEntities()) {
			if(!(e instanceof Shulker)) continue;
			Shulker s = (Shulker)e;
			if(!s.getPersistentDataContainer().has(WandKeys.SHULKER, PersistentDataType.STRING)) continue;
			s.remove();
		}
	}
}
