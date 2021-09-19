package ru.dragon_land.texhik.reloot.listeners;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ru.dragon_land.texhik.reloot.ReLoot;

public class GPClaimsHandler implements ClaimsHandler {

    private final GriefPrevention griefPrevention;

    public GPClaimsHandler(GriefPrevention griefPrevention) {
        this.griefPrevention = griefPrevention;
        ReLoot.logger.info("GriefPrevention found. Chests outside of it claims will be protected.");
    }

    @Override
    public boolean allowDestruction(Block block, Player player) {
        if (GriefPrevention.instance.claimsEnabledForWorld(block.getWorld())) {
            Claim claim = griefPrevention.dataStore.getClaimAt(block.getLocation(), false, null);
            //prevent destroying loot chests outside of claims
            //TODO config option
            return claim != null;
        }
        return true;
    }
}
