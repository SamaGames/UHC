package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class UHCEntityTargetEvent implements Listener
{
    @EventHandler
    public void event(EntityTargetEvent event)
    {
        if(event.getTarget() != null)
        {
            if(event.getTarget().getType() == EntityType.PLAYER)
            {
                if(!UHC.getPlugin().getArena().hasPlayer(event.getTarget().getUniqueId()))
                    event.setCancelled(true);
            }
        }
    }
}
