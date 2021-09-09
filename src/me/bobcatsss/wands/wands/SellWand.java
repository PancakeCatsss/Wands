package me.bobcatsss.wands.wands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

public class SellWand implements Wand, Listener {
	
	private List<Material> containerTypes;
	private final Wands plugin = Wands.getInstance();
	private DecimalFormat df = new DecimalFormat("#.##");

	public SellWand() {
		Bukkit.getPluginManager().registerEvents(this, Wands.getInstance());
		containerTypes = new ArrayList<>();
		containerTypes.add(Material.CHEST);
		containerTypes.add(Material.TRAPPED_CHEST);
		containerTypes.add(Material.HOPPER);
		containerTypes.add(Material.BARREL);
	}

	@Override
	public WandType getType() {
		return WandType.SELL;
	}

	@Override
	public String getName() {
		return "Sell Wand";
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
		lore.add(ChatUtils.color("&eRight click a chest to sell"));
		lore.add(ChatUtils.color("&ethe contents"));
		lore.add(ChatUtils.color("&cWill only sell what can be sold in &f/shop"));
		meta.setDisplayName(ChatUtils.color("&e&lSell Wand"));
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(WandKeys.WAND, PersistentDataType.STRING, WandType.SELL.name());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		return stack;
	}
	
	public boolean isSellWand(ItemStack stack) {
		if(stack == null || stack.getType() != Material.STICK || !stack.hasItemMeta()) return false;
		if(!stack.getItemMeta().getPersistentDataContainer().has(WandKeys.WAND, PersistentDataType.STRING)) return false;
		String type = stack.getItemMeta().getPersistentDataContainer().get(WandKeys.WAND, PersistentDataType.STRING);
		return type.equalsIgnoreCase("SELL");
	}
	
	public void handle(Player player, Block block) {
		if(Wands.getInstance().getEco() == null) {
			player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cSomething went wrong and to protect your items you cannot sell at this time&f."));
			return;
		}
		if(!(block.getState() instanceof Container)) return;
		if(!containerTypes.contains(block.getType())) return;
		BlockState state = block.getState();
		Container chest = (Container) state;
		double price = 0;
		int items = 0;
		for(int i = 0; i < chest.getInventory().getSize(); i++) {
			if(!chest.getInventory().getViewers().isEmpty()) {
				player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &cYou can't view the chest while selling items from it"));
				return;
			}
			ItemStack stack = chest.getInventory().getItem(i);
			if(stack == null) continue;
			if(!Wands.getInstance().getShopAPI().canSell(stack)) continue;
			items += stack.getAmount();
			price += Wands.getInstance().getShopAPI().getSellPrice(stack);
			chest.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		if(price != 0) {
			Wands.getInstance().getEco().depositPlayer(player, price);
		    player.sendMessage(ChatUtils.color("&eCatsCraft &6>> &aYou sold &f" + items + " &aitems for &2$&f" + df.format(price)));
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getHand() != EquipmentSlot.HAND) return;
		Player player = event.getPlayer();
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if(inHand == null) return;
		if(!isSellWand(inHand)) return;
		if(!event.hasBlock()) return;
		Block block = event.getClickedBlock();
		if(!(block.getState() instanceof Container)) return;
		if(!containerTypes.contains(block.getType())) return;
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		if(!plugin.getSkyblockUtils().canUse(player, block.getLocation())) return;
		handle(player, block);
	}

}
