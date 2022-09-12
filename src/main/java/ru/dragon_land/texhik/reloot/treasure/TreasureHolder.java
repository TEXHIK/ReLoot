package ru.dragon_land.texhik.reloot.treasure;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import ru.dragon_land.texhik.reloot.DataStore;
import ru.dragon_land.texhik.reloot.ReLoot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static ru.dragon_land.texhik.reloot.treasure.Treasure.EMPTY_TREASURE;

public class TreasureHolder {
    Map<String, Treasure> treasures;
    private static TreasureHolder instance;
    private DataStore store;
    private final Logger logger;

    private TreasureHolder() {
        logger = ReLoot.logger;
        treasures = new HashMap<>();
    }

    public static TreasureHolder getInstance() {
        if (instance == null) instance = new TreasureHolder();
        return instance;
    }

    public boolean interacted(Block block, Player player) {
        return treasures.getOrDefault(getTreasureUID(block), EMPTY_TREASURE).isOpenedBy(player);
    }

    public void interact(Block block, Player player) {
        Chest chest = (Chest) block.getState();
        LootTable lootTable = chest.getLootTable();
        String treasureUID = getTreasureUID(block);
        if (lootTable == null) {
            chest.setLootTable(getLoot(block));
            chest.update();
            treasures.get(treasureUID).addPlayer(player);
        } else {
            Treasure treasure = new Treasure(lootTable);
            treasure.addPlayer(player);
            treasures.put(treasureUID, treasure);
            logger.info("Created new treasure for " + lootTable.getKey() + " at " + block.getX() + ';' + block.getY() + ';' + block.getZ());
        }
    }

    public void remove(Block block) {
        String treasureUID = getTreasureUID(block);
        treasures.remove(treasureUID);
        logger.info("Treasure " + treasureUID + " removed (chest destroyed)");
    }

    public boolean hasLoot(Block block) {
        return treasures.containsKey(getTreasureUID(block));
    }

    public LootTable getLoot(Block block) {
        return treasures.getOrDefault(getTreasureUID(block), EMPTY_TREASURE).getLootTable();
    }

    public void load() {
        treasures = new HashMap<>(store.loadTreasures());
    }

    public void save() {
        store.saveTreasures(Collections.unmodifiableMap(treasures));
    }

    public void setStore(DataStore newStore) {
        if (store != null) {
            save();
        }
        store = newStore;
        load();
    }

    private String getTreasureUID(Block block) {
        return block.getWorld().getUID().toString()
                + '|' + block.getX()
                + '|' + block.getY()
                + '|' + block.getZ();
    }
}