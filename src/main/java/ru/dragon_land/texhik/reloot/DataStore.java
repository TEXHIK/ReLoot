package ru.dragon_land.texhik.reloot;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import ru.dragon_land.texhik.reloot.treasure.Treasure;
import sun.jvm.hotspot.oops.ReceiverTypeData;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.*;
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
        return loadQueryExecutor();
    }

    public void saveTreasures(Map<String, Treasure> treasures) {
        logger.info("dummy save: " + treasures.size());
        saveQueryExecutor(treasures);
    }

    private Map<String, Treasure> loadQueryExecutor() {

        Map<String, Treasure> map = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url)) {
            Statement treasureStatement = connection.createStatement();
            treasureStatement.execute("select * from treasures;");

            ResultSet treasureResultSet = treasureStatement.getResultSet();
            while (treasureResultSet.next()) {
                String treasureId = treasureResultSet.getString("id");
                map.put(treasureId, new Treasure(Bukkit.getLootTable(getKey(treasureResultSet.getString("loot_table"))), getPlayers(connection, treasureId)));
            }

        } catch (SQLException ex) {
            logger.log(SEVERE, "error save/load treasure", ex);
        }
        return map;
    }

    private Set<UUID> getPlayers(Connection connection, String treasureId) {
        Set<UUID> players = new HashSet<>();
        try (PreparedStatement playerStatement = connection.prepareStatement("select player_uuid from players where treasure_id=?");) {
            playerStatement.setObject(1, treasureId);
            playerStatement.execute();
            ResultSet playerResultSet = playerStatement.getResultSet();
            while (playerResultSet.next()) {
                players.add(UUID.fromString(playerResultSet.getString("player_uuid")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return players;
    }

    private NamespacedKey getKey(String line) {
        String[] loot_tables = line.split(":");
        return new NamespacedKey(loot_tables[0], loot_tables[1]);
    }

    private void saveQueryExecutor(Map<String, Treasure> treasures) {
        try (Connection connection = DriverManager.getConnection(url)) {

            treasures.forEach((k, v) -> {
                try {
                    PreparedStatement treasureStatement = connection.prepareStatement("insert into treasures (id, loot_table) values (?, ?);");
                    PreparedStatement playerStatement = connection.prepareStatement("insert into players (treasure_id, player_uuid) values (?, ?);");

                    treasureStatement.setObject(1, k);
                    treasureStatement.setObject(2, v.getLootTable().getKey());


                    if (v.getPlayers().size() > 0) {
                        for (UUID player : v.getPlayers()) {
                            playerStatement.setObject(1, k);
                            playerStatement.setObject(2, player);
                            playerStatement.execute();
                        }
                    }
                    treasureStatement.execute();

                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            });


        } catch (SQLException ex) {
            logger.log(SEVERE, "error save/load treasure", ex);
        }
    }
}