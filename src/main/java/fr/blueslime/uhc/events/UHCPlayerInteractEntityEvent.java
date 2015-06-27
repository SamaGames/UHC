package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class UHCPlayerInteractEntityEvent implements Listener
{
    @EventHandler
    public void event(PlayerInteractEntityEvent event)
    {
        if(!UHC.getPlugin().getArena().hasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }
}
