package ru.dragon_land.texhik.reloot.treasure;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class Treasure {
    private LootTable lootTable;
    private final Set<UUID> players;
    public static final Treasure EMPTY_TREASURE = new Treasure(Collections.emptySet()){
        @Override
        public void addPlayer(Player player) {
            throw new UnsupportedOperationException("you can not add players to empty treasure!");
        }

        @Override
        public void addPlayer(String playerUUID) {
            throw new UnsupportedOperationException("you can not add players to empty treasure!");
        }
    };

    private Treasure(Set<UUID> players) {
        this.players = players;
    }

    public Treasure(String lootTableKey) {
        var key = NamespacedKey.fromString(lootTableKey);
        this.lootTable = Bukkit.getLootTable(requireNonNull(key));
        players = new HashSet<>();
    }

    public Treasure(LootTable lootTable) {
        this.lootTable = lootTable;
        players = new HashSet<>();
    }

    public boolean isOpenedBy(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void addPlayer(String playerUUID) {
        players.add(UUID.fromString(playerUUID));
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

}
