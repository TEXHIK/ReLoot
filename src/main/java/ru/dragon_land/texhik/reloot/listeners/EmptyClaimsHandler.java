package ru.dragon_land.texhik.reloot.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ru.dragon_land.texhik.reloot.ReLoot;

public class EmptyClaimsHandler implements ClaimsHandler {

    public EmptyClaimsHandler() {
        ReLoot.logger.warning("No claim plugins found! Anyone could destroy treasures!");
    }

    @Override
    public boolean allowDestruction(Block block, Player player) {
        return true;
    }
}
