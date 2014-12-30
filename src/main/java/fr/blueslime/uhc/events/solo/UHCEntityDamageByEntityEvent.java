package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.UHC;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class UHCEntityDamageByEntityEvent implements Listener
{
    @EventHandler
    public void event(EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() == EntityType.PLAYER)
        {
            if(!UHC.getPlugin().getArenaSolo().hasPlayer(event.getDamager().getUniqueId()))
                event.setCancelled(true);
            else if(UHC.getPlugin().getArenaSolo().isSpectator(event.getDamager().getUniqueId()))
                event.setCancelled(true);
        }
        
        if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER)
        {
            if(!UHC.getPlugin().getArenaSolo().isFirstMinute())
            {
                event.setCancelled(true);
            }
            
            if(event.getDamager().getType() == EntityType.PLAYER)
            {
                if(!UHC.getPlugin().getArenaSolo().hasPlayer(event.getDamager().getUniqueId()))
                    event.setCancelled(true);
                else if(UHC.getPlugin().getArenaSolo().isSpectator(event.getDamager().getUniqueId()))
                    event.setCancelled(true);
            }
        }
        
    }
}
