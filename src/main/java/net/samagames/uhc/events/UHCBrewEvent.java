package net.samagames.uhc.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

public class UHCBrewEvent implements Listener
{
    @EventHandler
    public void event(BrewEvent event)
    {
        if(event.getContents().getIngredient().getType() == Material.GLOWSTONE_DUST)
            event.setCancelled(true);
    }
}
