package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
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
        ArenaCommon arena = UHC.getPlugin().getArena();
        
        if(arena.hasPlayer(event.getPlayer().getUniqueId()))
        {
            ArenaPlayer player = arena.getPlayer(event.getPlayer().getUniqueId());
            
            if(arena.isGameStarted())
            {
                //GameAPI.addRejoinList(event.getPlayer().getUniqueId(), 600);
                //UHC.getPlugin().getArena().playerDisconnect(event.getPlayer().getUniqueId());
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
        
        UHC.getPlugin().getArena().getObjectiveSign().removeReceiver(event.getPlayer());
        GameAPI.getManager().refreshArena(arena);
    }
}
