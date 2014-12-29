package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.Arena;
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
        Arena arena = UHC.getPlugin().getArena();
        
        if(arena.hasPlayer(event.getPlayer().getUniqueId()))
        {
            ArenaPlayer player = arena.getPlayer(event.getPlayer().getUniqueId());
            
            if(arena.isGameStarted())
            {
                //GameAPI.addRejoinList(event.getPlayer().getUniqueId(), 600);
                //UHC.getPlugin().getArena().playerDisconnect(event.getPlayer().getUniqueId());
                arena.lose(player.getPlayerID(), true);
            }
            else
            {
                arena.removePlayer(player);
            }
        }
        
        GameAPI.getManager().refreshArena(arena);
    }
}
