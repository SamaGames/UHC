package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.Arena;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class UHCFoodLevelChangeEvent implements Listener
{
    @EventHandler
    public void event(FoodLevelChangeEvent event)
    {
        event.setCancelled(true);
        
        if(event.getEntity().getType() == EntityType.PLAYER)
        {
            Arena arena = UHC.getPlugin().getArena();

            if(arena.hasPlayer(event.getEntity().getUniqueId()) && arena.isGameStarted())
            {
                event.setCancelled(false);
            }
        }
    }
}
