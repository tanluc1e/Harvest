package me.tanluc.gatlua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Gatlua extends JavaPlugin {
    static final ArrayList<Material> CROPS = new ArrayList<>();

    static final ArrayList<Material> TALL_CROPS = new ArrayList<>();

    static FileConfiguration config;

    private static Logger logger;

    private static final String PREFIX = "[GatLua] ";

    private static PluginManager pm = Bukkit.getPluginManager();

    public void onEnable() {
        CROPS.addAll(Arrays.asList(new Material[] { Material.WHEAT, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.MELON, Material.PUMPKIN, Material.NETHER_WART, Material.COCOA }));
        TALL_CROPS.addAll(Arrays.asList(new Material[] { Material.BAMBOO, Material.CACTUS, Material.SUGAR_CANE }));
        saveDefaultConfig();
        config = getConfig();
        getServer().getPluginManager().registerEvents(new HarvestListener(), (Plugin)this);
        logger = getServer().getLogger();
    }

    public static void fireEvent(Event event) {
        pm.callEvent(event);
    }

    public static void log(Level level, String message) {
        logger.log(level, "[Harvester] " + message);
    }

    public static void log(String message) {
        logger.log(Level.INFO, "[Harvester] " + message);
    }
}
