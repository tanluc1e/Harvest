package me.tanluc.gatlua.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HarvestEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    private Player player;

    private Block block;

    private static final HandlerList HANDLERS = new HandlerList();

    public HarvestEvent(Block block, Player player) {
        this.block = block;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
