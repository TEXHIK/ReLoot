package ru.dragon_land.texhik.reloot;

import ru.dragon_land.texhik.reloot.treasure.Treasure;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class DataStore {

    private final String url;
    private final Logger logger;

    public DataStore(String filename) {
        this.url = "jdbc:sqlite:" + ReLoot.getPluginDataFolder().getPath() + '/' + filename;
        logger = ReLoot.instance.getLogger();
    }

    public void init() {
        final File pluginDataFolder = ReLoot.getPluginDataFolder();
        if (!pluginDataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pluginDataFolder.mkdirs();
        }
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.createStatement().execute("create table if not exists treasures(id text primary key, loot_table text)");
            connection.createStatement().execute("create table if not exists players(id integer primary key autoincrement, treasure_id text references treasures, player_uuid text)");
        } catch (SQLException ex) {
            logger.log(SEVERE, "error creating db", ex);
        }
    }

    public Map<String, Treasure> loadTreasures() {
        logger.info("dummy load!");
        return Collections.emptyMap();
    }

    public void saveTreasures(Map<String, Treasure> treasures) {
        logger.info("dummy save: " + treasures.size());
    }
}