package me.bobcatsss.wands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bobcatsss.wands.Wands;

public class CommandWand implements CommandExecutor {
	
	private Wands plugin = Wands.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player player = (Player)sender;
	    if(args.length == 0) {
	    	player.openInventory(plugin.getWandShop().getInventory());
	    	return true;
	    }
	    if(!player.hasPermission("wands.admin")) {
	    	player.openInventory(plugin.getWandShop().getInventory());
	    	return true;
	    }
	    String type = args[0];
	    if(plugin.getWand(type) == null) {
	    	player.sendMessage("Invalid wand type");
	    	return true;
	    }
	    player.getInventory().addItem(plugin.getWand(type).getItem());
		return true;
	}

}
