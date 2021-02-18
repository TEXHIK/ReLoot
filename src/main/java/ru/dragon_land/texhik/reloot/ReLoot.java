package ru.dragon_land.texhik.reloot;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import ru.dragon_land.texhik.reloot.listeners.*;
import ru.dragon_land.texhik.reloot.treasure.TreasureHolder;

import java.io.File;
import java.util.logging.Logger;

@Plugin(name = "ReLoot", version = "1.0")
@ApiVersion(ApiVersion.Target.DEFAULT)
@Author("TEXHIK")
@SoftDependency("GriefPrevention")
public class ReLoot extends JavaPlugin {
    public static final String DATA_FOLDER = "plugins/reLoot";
    public static ReLoot instance;
    public static Logger log;
    private ChestInteractListener listener;
    private DataStore dataStore;

    @Override
    public void onEnable() {
        instance = this;
        log = this.getLogger();
        GriefPrevention griefPrevention = (GriefPrevention) getServer().getPluginManager().getPlugin("GriefPrevention");
        ClaimsHandler handler;
        if (griefPrevention != null) {
            log.info("GriefPrevention found. Chests outside of it claims will be protected.");
            handler = new GPClaimsHandler(griefPrevention);
        } else {
            log.warning("No claim plugins found! Anyone could destroy treasures!");
            handler = new EmptyClaimsHandler();
        }
        dataStore = new DataStore("data.db");
        dataStore.init();
        TreasureHolder.getInstance().load(dataStore);
        getServer().getPluginManager().registerEvents(new ChestInteractListener(handler), this);
        getServer().getPluginManager().registerEvents(new SaveListener(), this);
    }

    @Override
    public void onDisable() {
        TreasureHolder.getInstance().save(dataStore);
    }

    public static File getPluginDataFolder() {
        return new File(DATA_FOLDER);
    }

    public static DataStore getDataStore() {
        return instance.dataStore;
    }
}