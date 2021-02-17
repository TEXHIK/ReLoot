package ru.dragon_land.texhik.reloot.treasure;

import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import ru.dragon_land.texhik.reloot.ReLoot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Treasure {
    private LootTable lootTable;
    private final Set<UUID> players;
    public static final Treasure EMPTY_TREASURE = new Treasure();

    private Treasure() {
        players = Collections.emptySet();
    }

    public Treasure(LootTable lootTable) {
        ReLoot.log.info("created new treasure for " + lootTable.getKey().toString());
        this.lootTable = lootTable;
        players = new HashSet<>();
    }

    public boolean isOpenedBy(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public LootTable getLootTable() {
        return lootTable;
    }
    //dragon-land.no-ip.org
}
