package fr.blueslime.uhc.events;

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
            if(!UHC.getPlugin().getArena().hasPlayer(event.getDamager().getUniqueId()))
                event.setCancelled(true);
            else if(UHC.getPlugin().getArena().isSpectator(event.getDamager().getUniqueId()))
                event.setCancelled(true);
        }
        
        if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER)
        {
            if(!UHC.getPlugin().getArena().isFirstMinute())
            {
                event.setCancelled(true);
            }
            
            if(event.getDamager().getType() == EntityType.PLAYER)
            {
                if(!UHC.getPlugin().getArena().hasPlayer(event.getDamager().getUniqueId()))
                    event.setCancelled(true);
                else if(UHC.getPlugin().getArena().isSpectator(event.getDamager().getUniqueId()))
                    event.setCancelled(true);
                else if(UHC.getPlugin().getArena().getPlayer(event.getEntity().getUniqueId()).hasTeam() && UHC.getPlugin().getArena().getPlayer(event.getEntity().getUniqueId()).getTeam().hasPlayer(event.getDamager().getUniqueId()))
                    event.setCancelled(true);
            }
        }
        
    }
}
