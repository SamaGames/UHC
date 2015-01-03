package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
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
        ArenaCommon arena = UHC.getPlugin().getArena();
        
        if(arena.hasPlayer(player.getUniqueId()))
        {
            event.setCancelled(false);
        }
    }
}
