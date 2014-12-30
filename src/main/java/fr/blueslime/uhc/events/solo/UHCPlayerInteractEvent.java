package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.events.*;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.gui.GuiSelectTeam;
import net.samagames.gameapi.GameAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class UHCPlayerInteractEvent implements Listener
{
    @EventHandler
    public void event(PlayerInteractEvent event)
    {
        if(!UHC.getPlugin().getArenaSolo().hasPlayer(event.getPlayer().getUniqueId()))
        {
            event.setCancelled(true);
        }
        
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {      
            if(event.getItem() != null)
            {
                if(!UHC.getPlugin().getArenaSolo().isGameStarted())
                {         
                    event.setCancelled(true);
                    
                    if(event.getItem().getType() == Material.WOOD_DOOR)
                        GameAPI.kickPlayer(event.getPlayer());
                }
                else
                {
                    if(!UHC.getPlugin().getArenaSolo().hasPlayer(event.getPlayer().getUniqueId()))
                    {
                        event.setCancelled(true);

                        if(event.getItem().getType() == Material.WOOD_DOOR)
                            GameAPI.kickPlayer(event.getPlayer());
                    }
                }
            }
        }
    }
}
