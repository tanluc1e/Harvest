package me.tanluc.gatlua.util;

import org.bukkit.block.Block;

public class LocationUtils {
    public static Block getBlockAbove(Block block, int height) {
        return block.getWorld().getBlockAt(block.getX(), block.getY() + height, block.getZ());
    }

    public static Block getBlockBelow(Block block, int height) {
        return block.getWorld().getBlockAt(block.getX(), block.getY() - height, block.getZ());
    }
}
