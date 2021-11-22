package me.tanluc.gatlua;

import me.tanluc.gatlua.event.HarvestEvent;
import me.tanluc.gatlua.event.TallHarvestEvent;
import me.tanluc.gatlua.util.LocationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

class HarvestListener implements Listener {
    private ArrayList<Block> harvestedBlocks = new ArrayList<>();

    private Map<Block, Integer> harvestedTallBlocks = new HashMap<>();

    private final Random RANDOM = new Random();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR) && event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.AIR))
            try {
                Block block = event.getClickedBlock();
                BlockData blockData = block.getBlockData();
                if (Gatlua.CROPS.contains(block.getType())) {
                    if (blockData instanceof Ageable) {
                        if (((Ageable)blockData).getAge() == ((Ageable)blockData).getMaximumAge())
                            Gatlua.fireEvent((Event)new HarvestEvent(event.getClickedBlock(), event.getPlayer()));
                    } else {
                        Gatlua.fireEvent((Event)new HarvestEvent(event.getClickedBlock(), event.getPlayer()));
                    }
                } else if (Gatlua.TALL_CROPS.contains(block.getType())) {
                    int blocks = 1;
                    while (LocationUtils.getBlockAbove(block, blocks).getType() == block.getType())
                        blocks++;
                    Gatlua.fireEvent((Event)new TallHarvestEvent(event.getClickedBlock(), event.getPlayer(), blocks));
                }
            } catch (Exception ignored) {
                Gatlua.log(Level.SEVERE, "Lỗi");
            }
    }

    @EventHandler
    public void onHarvest(HarvestEvent event) {
        BlockBreakEvent breakEvent = new BlockBreakEvent(event.getBlock(), event.getPlayer());
        if (event instanceof TallHarvestEvent) {
            this.harvestedTallBlocks.put(event.getBlock(), Integer.valueOf(((TallHarvestEvent)event).getBlockCount()));
        } else {
            this.harvestedBlocks.add(event.getBlock());
        }
        Material originalType = event.getBlock().getType();
        if (Gatlua.config.getBoolean("permission-required")) {
            if (event.getPlayer().hasPermission("gatlua.use"))
                doHarvest(event, breakEvent, originalType);
        } else {
            doHarvest(event, breakEvent, originalType);
        }
    }

    private void doHarvest(HarvestEvent event, BlockBreakEvent breakEvent, Material originalType) {
        Gatlua.fireEvent((Event)breakEvent);
        if (!breakEvent.isCancelled())
            if (event instanceof TallHarvestEvent) {
                TallHarvestEvent tallEvent = (TallHarvestEvent)event;
                for (int i = tallEvent.getBlockCount(); i >= 0; i--) {
                    Block above = LocationUtils.getBlockAbove(event.getBlock(), i);
                    above.setType(Material.AIR);
                }
                if (originalType == Material.BAMBOO && LocationUtils.getBlockBelow(event.getBlock(), 1).getType() != Material.BAMBOO)
                    event.getBlock().setType(Material.BAMBOO_SAPLING);
            } else if (originalType != Material.MELON && originalType != Material.PUMPKIN) {
                event.getBlock().setType(originalType);
                if (event.getBlock() instanceof Ageable)
                    ((Ageable)event.getBlock()).setAge(0);
            } else {
                event.getBlock().setType(Material.AIR);
            }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.harvestedBlocks.contains(event.getBlock())) {
            if (!event.isCancelled()) {
                event.setDropItems(false);
                givePlayerItems(event.getPlayer(), event.getBlock());
            }
            this.harvestedBlocks.remove(event.getBlock());
        } else if (this.harvestedTallBlocks.containsKey(event.getBlock())) {
            if (!event.isCancelled()) {
                event.setDropItems(false);
                givePlayerItems(event.getPlayer(), event.getBlock(), ((Integer)this.harvestedTallBlocks.get(event.getBlock())).intValue());
            }
            this.harvestedTallBlocks.remove(event.getBlock());
        }
    }

    private void givePlayerItems(Player harvester, Block crop, int count) {
        ItemStack item = new ItemStack(crop.getType(), count);
        if (!harvester.getInventory().addItem(new ItemStack[] { item }).isEmpty())
            crop.getWorld().dropItemNaturally(crop.getLocation(), item);
        harvester.playSound(harvester.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    }

    private void givePlayerItems(Player harvester, Block crop) {
        ArrayList<ItemStack> itemsToInventory = new ArrayList<>();
        Material type = crop.getType();
        switch (type) {
            case WHEAT:
                itemsToInventory.add(new ItemStack(Material.WHEAT, 1));
                itemsToInventory.add(new ItemStack(Material.WHEAT_SEEDS, this.RANDOM.nextInt(3)));
                break;
            case BEETROOTS:
                itemsToInventory.add(new ItemStack(Material.BEETROOT, 1));
                itemsToInventory.add(new ItemStack(Material.BEETROOT_SEEDS, this.RANDOM.nextInt(3)));
                break;
            case CARROTS:
                itemsToInventory.add(new ItemStack(Material.CARROT, this.RANDOM.nextInt(5)));
                break;
            case POTATOES:
                itemsToInventory.add(new ItemStack(Material.POTATO, this.RANDOM.nextInt(4)));
                if (this.RANDOM.nextInt(100) < 2)
                    itemsToInventory.add(new ItemStack(Material.POISONOUS_POTATO, 1));
                break;
            case NETHER_WART:
                itemsToInventory.add(new ItemStack(Material.NETHER_WART, this.RANDOM.nextInt(3) + 1));
                break;
            case MELON:
                itemsToInventory.add(new ItemStack(Material.MELON_SLICE, this.RANDOM.nextInt(5) + 3));
                break;
            case PUMPKIN:
                itemsToInventory.add(new ItemStack(Material.PUMPKIN, 1));
                break;
            case COCOA:
                itemsToInventory.add(new ItemStack(Material.COCOA_BEANS, 2));
                break;
        }
        for (ItemStack item : itemsToInventory) {
            if (!harvester.getInventory().addItem(new ItemStack[] { item }).isEmpty())
                crop.getWorld().dropItemNaturally(crop.getLocation(), item);
        }
        harvester.playSound(harvester.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    }
}
