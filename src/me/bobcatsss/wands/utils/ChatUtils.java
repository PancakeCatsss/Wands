package me.bobcatsss.wands.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {
	
	public static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
