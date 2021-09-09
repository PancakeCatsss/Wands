package me.bobcatsss.wands.utils;

import org.bukkit.inventory.ItemStack;

import com.pablo67340.guishop.api.GuiShopAPI;

public class ShopAPI {
	
	public boolean canSell(ItemStack stack) {
		return GuiShopAPI.canBeSold(stack);
	}
	
	public double getSellPrice(ItemStack stack) {
		return GuiShopAPI.getSellPrice(stack, stack.getAmount()).doubleValue();
	}

}
