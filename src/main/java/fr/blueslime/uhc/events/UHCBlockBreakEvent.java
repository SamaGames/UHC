package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class UHCBlockBreakEvent implements Listener
{
    @EventHandler
    public void event(BlockBreakEvent event)
    {
        if(!UHC.getPlugin().getArena().isGameStarted())
        {
            event.setCancelled(true);
        }
        else if(!UHC.getPlugin().getArena().hasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else
        {
            event.setCancelled(false);
        }
    }
}
