package me.bobcatsss.wands.wandutils;

import org.bukkit.NamespacedKey;

import me.bobcatsss.wands.Wands;

public class WandKeys {

    public final static NamespacedKey WAND;
    public final static NamespacedKey NODROP;
    public final static NamespacedKey SHULKER;
    public final static NamespacedKey COOLDOWN;
    public final static NamespacedKey SLIME_BLOCKER;

    static {
        WAND = new NamespacedKey(Wands.getInstance(), "wand");
        NODROP = new NamespacedKey(Wands.getInstance(), "nodrop");
        SHULKER = new NamespacedKey(Wands.getInstance(), "shulker");
        COOLDOWN = new NamespacedKey(Wands.getInstance(), "cooldown");
        SLIME_BLOCKER = new NamespacedKey(Wands.getInstance(), "slime_blocker");
    }
}

