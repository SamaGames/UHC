package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
import fr.blueslime.uhc.arena.ArenaPlayer;
import net.samagames.gameapi.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UHCPlayerQuitEvent implements Listener
{
    @EventHandler
    public void event(PlayerQuitEvent event)
    {
        ArenaCommon arena = UHC.getPlugin().getArena();
        
        if(arena.hasPlayer(event.getPlayer().getUniqueId()))
        {
            ArenaPlayer player = arena.getPlayer(event.getPlayer().getUniqueId());
            
            if(arena.isGameStarted())
            {
                UHC.getPlugin().getArena().playerDisconnect(event.getPlayer().getUniqueId());
            }
            else
            {
                arena.removePlayer(player);
            }
        }
        
        UHC.getPlugin().getArena().getObjectiveSign().removeReceiver(event.getPlayer());
        GameAPI.getManager().refreshArena(arena);
    }
}
