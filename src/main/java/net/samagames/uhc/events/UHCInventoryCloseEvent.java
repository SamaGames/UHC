package net.samagames.uhc.events;

import net.samagames.uhc.UHC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class UHCInventoryCloseEvent implements Listener
{
    @EventHandler
    public void event(InventoryCloseEvent event)
    {
        if(UHC.getPlugin().getPlayerGui(event.getPlayer().getUniqueId()) != null)
        {
            UHC.getPlugin().removePlayerFromList(event.getPlayer().getUniqueId());
        }
    }
}
