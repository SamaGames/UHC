package fr.blueslime.uhc.events.team;

import fr.blueslime.uhc.events.solo.*;
import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaGameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class UHCPlayerDropItemEvent implements Listener
{
    @EventHandler
    public void event(PlayerDropItemEvent event)
    {        
        Player player = event.getPlayer();
        ArenaGameTeam arena = UHC.getPlugin().getArenaTeam();
        
        if(!arena.isGameStarted())
        {
            event.setCancelled(true);
        }
        else
        {            
            if(!arena.hasPlayer(player.getUniqueId()))
            {
                event.setCancelled(true);
            }
        }
    }
}
