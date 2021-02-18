package ru.dragon_land.texhik.reloot;

import ru.dragon_land.texhik.reloot.treasure.Treasure;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class DataStore {

   private static final String TREASURES_SELECT = "select treasures.id, loot_table loot, players.player_uuid player from treasures join players on treasures.id = players.treasure_id;";
   private static final String INSERT_TREASURES = "insert into treasures (id, loot_table) values (?, ?) ON CONFLICT DO NOTHING;";
   private static final String INSERT_PLAYERS = "insert into players (treasure_id, player_uuid) values (?, ?) ON CONFLICT DO NOTHING;;";
   private static final String CREATE_TREASURES = "create table if not exists treasures(id text primary key, loot_table text)";
   private static final String CREATE_PLAYERS = "create table if not exists players(treasure_id text references treasures, player_uuid text, primary key (treasure_id, player_uuid))";
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
            connection.createStatement().execute(CREATE_TREASURES);
            connection.createStatement().execute(CREATE_PLAYERS);
        } catch (SQLException ex) {
            logger.log(SEVERE, "error creating db", ex);
        }
    }

    public Map<String, Treasure> loadTreasures() {
        Map<String, Treasure> treasures = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            statement.execute(TREASURES_SELECT);
            final ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                final String treasureId = resultSet.getString("id");
                final String lootTableKey = resultSet.getString("loot");
                treasures.computeIfAbsent(treasureId, s -> new Treasure(lootTableKey))
                        .addPlayer(resultSet.getString("player"));
            }
            statement.close();
        } catch (SQLException ex) {
            logger.log(SEVERE, "Failed to load treasures: ", ex);
        }
        return treasures;
    }

    public void saveTreasures(Map<String, Treasure> treasures) {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement treasureStatement = connection.prepareStatement(INSERT_TREASURES);
            PreparedStatement playerStatement = connection.prepareStatement(INSERT_PLAYERS);
            for (String id : treasures.keySet()) {
                Treasure treasure = treasures.get(id);
                treasureStatement.setString(1, id);
                treasureStatement.setString(2, treasure.getLootTable().getKey().toString());
                treasureStatement.execute();

                playerStatement.setString(1, id);
                for (UUID playerUuid : treasure.getPlayers()) {
                    playerStatement.setString(2, playerUuid.toString());
                    playerStatement.execute();
                }
            }

        } catch (SQLException ex) {
            logger.log(SEVERE, "error save/load treasure", ex);
        }
    }

}