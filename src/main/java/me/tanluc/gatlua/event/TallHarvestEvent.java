package me.tanluc.gatlua.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TallHarvestEvent extends HarvestEvent {
    private int blockCount;

    public TallHarvestEvent(Block block, Player player, int blockCount) {
        super(block, player);
        this.blockCount = blockCount;
    }

    public int getBlockCount() {
        return this.blockCount;
    }
}
