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
        if (block.getType() != Material.CHEST) {
            return;
        }
        if (!claimsHandler.allowDestruction(block, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST || !(block.getState() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) block.getState();
        Player player = event.getPlayer();

        if (chest.getLootTable() == null && !treasureHolder.hasLoot(block)) {
            return;
        }

        if (treasureHolder.interacted(block, player)) {
            player.sendMessage("You've already looted this chest!");
        } else {
            player.sendMessage("You found a treasure");
            treasureHolder.interact(block, player);
        }
    }
}