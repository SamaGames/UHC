package net.samagames.uhc.events;

import net.samagames.uhc.UHC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class UHCEntityDamageEvent implements Listener
{
    @EventHandler
    public void event(EntityDamageEvent event)
    {
        if(event.getEntity().getType() == EntityType.PLAYER)
        {
            if(!UHC.getPlugin().getArena().isFirstMinute())
            {
                event.setCancelled(true);
            }
            
            if(UHC.getPlugin().getArena().isSpectator((Player) event.getEntity()))
            {
                event.setCancelled(true);
            }
        }
    }
}
