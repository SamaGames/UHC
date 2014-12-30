package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import org.bukkit.entity.EntityType;
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
            if(!UHC.getPlugin().getArenaSolo().isFirst30Seconds())
            {
                event.setCancelled(true);
            }
            
            if(UHC.getPlugin().getArenaSolo().isSpectator(event.getEntity().getUniqueId()))
            {
                event.setCancelled(true);
            }
        }
    }
}
