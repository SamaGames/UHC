package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaGameSolo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class UHCPlayerPickupItemEvent implements Listener
{
    @EventHandler
    public void event(PlayerPickupItemEvent event)
    {
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        ArenaGameSolo arena = UHC.getPlugin().getArenaSolo();
        
        if(arena.hasPlayer(player.getUniqueId()))
        {
            event.setCancelled(false);
        }
    }
}
