package ru.dragon_land.texhik.reloot.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.dragon_land.texhik.reloot.treasure.TreasureHolder;

public class ChestInteractListener implements Listener {
    private final TreasureHolder treasureHolder;
    private final ClaimsHandler claimsHandler;

    public ChestInteractListener(ClaimsHandler claimsHandler) {
        this.claimsHandler = claimsHandler;
        treasureHolder = TreasureHolder.getInstance();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChestBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() != Material.CHEST || !(block.getState() instanceof Chest chest)) {
            return;
        }

        if (chest.getLootTable() == null && !treasureHolder.hasLoot(block)) {
            return;
        }

        if (claimsHandler.allowDestruction(block, player)) {
            treasureHolder.remove(block);
        } else {
            event.setCancelled(true);
            //TODO localization
            player.sendMessage("Other players may find this treasure too. Please, leave it for them:)");
        }
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST || !(block.getState() instanceof Chest chest)) {
            return;
        }

        if (chest.getLootTable() == null && !treasureHolder.hasLoot(block)) {
            return;
        }

        Player player = event.getPlayer();
        if (!treasureHolder.interacted(block, player)) {
            treasureHolder.interact(block, player);
        }
    }
}