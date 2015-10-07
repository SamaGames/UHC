package net.samagames.uhc.events;

import net.samagames.uhc.UHC;
import net.samagames.uhc.arena.ArenaCommon;
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
        
        if(arena.hasPlayer(player))
        {
            event.setCancelled(false);
        }
    }
}
