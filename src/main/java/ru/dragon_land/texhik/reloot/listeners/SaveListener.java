package ru.dragon_land.texhik.reloot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import ru.dragon_land.texhik.reloot.ReLoot;
import ru.dragon_land.texhik.reloot.treasure.TreasureHolder;

public class SaveListener implements Listener {

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        TreasureHolder.getInstance().save(ReLoot.getDataStore());
    }

}
