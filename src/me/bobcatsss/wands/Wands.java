package me.bobcatsss.wands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.bobcatsss.wands.commands.CommandWand;
import me.bobcatsss.wands.handlers.CraftHandler;
import me.bobcatsss.wands.utils.ShopAPI;
import me.bobcatsss.wands.utils.SkyblockUtils;
import me.bobcatsss.wands.wands.BabyWand;
import me.bobcatsss.wands.wands.EntityPickupWand;
import me.bobcatsss.wands.wands.SellWand;
import me.bobcatsss.wands.wands.SlimeBlockerWand;
import me.bobcatsss.wands.wands.SlimeChunkWand;
import me.bobcatsss.wands.wands.WandShop;
import me.bobcatsss.wands.wandutils.Wand;
import net.milkbowl.vault.economy.Economy;

public class Wands extends JavaPlugin {
	
	private ShopAPI api;
	private static Wands instance;
	private List<Wand> wands = new ArrayList<>();
	private static Economy econ = null;
	private SkyblockUtils utils;
	private WandShop shop;
	
	@Override
	public void onEnable() {
		instance = this;
		if(Bukkit.getPluginManager().getPlugin("GUIShop") != null && Bukkit.getPluginManager().getPlugin("GUIShop").isEnabled()) {
			this.api = new ShopAPI();
		}
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		registerCommands();
		registerHandlers();
        this.shop = new WandShop();
        this.utils = new SkyblockUtils();
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
	private void registerHandlers() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new CraftHandler(), this);
		wands.add(new SellWand());
		wands.add(new EntityPickupWand());
		wands.add(new BabyWand());
		wands.add(new SlimeChunkWand());
		wands.add(new SlimeBlockerWand());
		
	}
	
	private void registerCommands() {
		getCommand("wands").setExecutor(new CommandWand());
	}
	
	public static Wands getInstance() {
		return instance;
	}
	
	public ShopAPI getShopAPI() {
		return api;
	}
	
	public SkyblockUtils getSkyblockUtils() {
		return utils;
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public Economy getEco() {
    	return econ;
    }
    
    public WandShop getWandShop() {
    	return shop;
    }
	
	public Wand getWand(String type) {
		for(Wand wand : wands) {
			if(wand.getType().name().equalsIgnoreCase(type)) return wand;
		}
		return null;
	}

}
