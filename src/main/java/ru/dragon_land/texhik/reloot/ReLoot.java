package ru.dragon_land.texhik.reloot;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import ru.dragon_land.texhik.reloot.listeners.*;
import ru.dragon_land.texhik.reloot.treasure.TreasureHolder;

import java.io.File;
import java.util.logging.Logger;

@Plugin(name = "ReLoot", version = "1.2")
@ApiVersion(ApiVersion.Target.v1_20)
@Author("TEXHIK")
@SoftDependency("GriefPrevention")
public class ReLoot extends JavaPlugin {
    public static final String DATA_FOLDER = "plugins/reLoot";
    public static ReLoot instance;
    public static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        GriefPrevention griefPrevention = (GriefPrevention) getServer().getPluginManager().getPlugin("GriefPrevention");
        ClaimsHandler handler = griefPrevention == null ? new EmptyClaimsHandler() : new GPClaimsHandler(griefPrevention);

        DataStore dataStore = new DataStore("data.db");
        dataStore.init();
        TreasureHolder.getInstance().setStore(dataStore);

        getServer().getPluginManager().registerEvents(new ChestInteractListener(handler), this);
        getServer().getPluginManager().registerEvents(new SaveListener(), this);
    }

    @Override
    public void onDisable() {
        TreasureHolder.getInstance().save();
        HandlerList.unregisterAll(this);
    }

    public static File getPluginDataFolder() {
        return new File(DATA_FOLDER);
    }

}