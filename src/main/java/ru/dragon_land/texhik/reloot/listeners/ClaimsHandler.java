package ru.dragon_land.texhik.reloot.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface ClaimsHandler {
    default boolean allowDestruction(Block block, Player player) {
        return true;
    }
}
