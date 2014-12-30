package fr.blueslime.uhc.events.team;

import fr.blueslime.uhc.events.solo.*;
import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaGameTeam;
import fr.blueslime.uhc.arena.ArenaPlayer;
import net.samagames.gameapi.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class UHCPlayerQuitEvent implements Listener
{
    @EventHandler
    public void event(PlayerQuitEvent event)
    {
        ArenaGameTeam arena = UHC.getPlugin().getArenaTeam();
        
        if(arena.hasPlayer(event.getPlayer().getUniqueId()))
        {
            ArenaPlayer player = arena.getPlayer(event.getPlayer().getUniqueId());
            
            if(arena.isGameStarted())
            {
                //GameAPI.addRejoinList(event.getPlayer().getUniqueId(), 600);
                //UHC.getPlugin().getArenaTeam().playerDisconnect(event.getPlayer().getUniqueId());
                arena.lose(player.getPlayerID(), true);
                
                for(ItemStack stack : event.getPlayer().getInventory().getContents())
                {
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), stack);
                }
            }
            else
            {
                arena.removePlayer(player);
            }
        }
        
        GameAPI.getManager().refreshArena(arena);
    }
}
