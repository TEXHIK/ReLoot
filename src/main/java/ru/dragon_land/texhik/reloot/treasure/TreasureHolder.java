package ru.dragon_land.texhik.reloot.treasure;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import ru.dragon_land.texhik.reloot.DataStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ru.dragon_land.texhik.reloot.treasure.Treasure.EMPTY_TREASURE;

public class TreasureHolder {
    Map<String, Treasure> treasures;
    private static TreasureHolder instance;

    private TreasureHolder() {
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
        if (lootTable == null) {
            chest.setLootTable(getLoot(block));
            chest.update();
            treasures.get(getTreasureUID(block)).addPlayer(player);
        } else {
            Treasure treasure = new Treasure(lootTable);
            treasure.addPlayer(player);
            treasures.put(getTreasureUID(block), treasure);
        }
    }

    public boolean hasLoot(Block block) {
        return treasures.containsKey(getTreasureUID(block));
    }

    public LootTable getLoot(Block block) {
        return treasures.getOrDefault(getTreasureUID(block), EMPTY_TREASURE).getLootTable();
    }

    public void load(DataStore store) {
        treasures = new HashMap<>(store.loadTreasures());
    }

    public void save(DataStore store) {
        store.saveTreasures(Collections.unmodifiableMap(treasures));
    }

    private String getTreasureUID(Block block) {
        return block.getWorld().getUID().toString()
                + '-' + block.getX()
                + '-' + block.getY()
                + '-' + block.getZ();
    }
}