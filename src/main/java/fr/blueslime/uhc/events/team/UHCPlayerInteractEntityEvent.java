package fr.blueslime.uhc.events.team;

import fr.blueslime.uhc.events.solo.*;
import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class UHCPlayerInteractEntityEvent implements Listener
{
    @EventHandler
    public void event(PlayerInteractEntityEvent event)
    {
        if(!UHC.getPlugin().getArenaTeam().hasPlayer(event.getPlayer().getUniqueId()))
        {
            event.setCancelled(true);
        }
    }
}
