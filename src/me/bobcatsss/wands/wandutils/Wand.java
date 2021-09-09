package me.bobcatsss.wands.wandutils;

import org.bukkit.inventory.ItemStack;

public interface Wand {
	
	public WandType getType();
	public String getName();
	public int getCost();
	public ItemStack getItem();
}
