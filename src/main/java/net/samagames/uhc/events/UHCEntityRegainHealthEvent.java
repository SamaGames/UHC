package net.samagames.uhc.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class UHCEntityRegainHealthEvent implements Listener
{
    @EventHandler
    public void event(EntityRegainHealthEvent event)
    {
        if(event.getRegainReason() == RegainReason.SATIATED)
            event.setCancelled(true);
    }
}
