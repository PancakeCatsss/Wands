package me.bobcatsss.wands.utils;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

public class SkyblockUtils {

	public boolean canUse(Player player, Location location) {
		Optional<Island> i = BentoBox.getInstance().getIslandsManager().getProtectedIslandAt(location);
		if(!i.isPresent()) return false;
		Island island = i.get();
		if(island.getMembers().containsKey(player.getUniqueId())) return true;
		return false;
	}
}
