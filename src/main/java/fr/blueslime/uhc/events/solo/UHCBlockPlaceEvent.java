package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class UHCBlockPlaceEvent implements Listener
{
    @EventHandler
    public void event(BlockPlaceEvent event)
    {
        if(!UHC.getPlugin().getArenaSolo().isGameStarted())
        {
            event.setCancelled(true);
        }
        else if(!UHC.getPlugin().getArenaSolo().hasPlayer(event.getPlayer().getUniqueId()))
        {
            event.setCancelled(true);
        }
        else
        {
            event.setCancelled(false);
        }
    }
}
