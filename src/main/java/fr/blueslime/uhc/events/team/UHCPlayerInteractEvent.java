package fr.blueslime.uhc.events.team;

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
        if(!UHC.getPlugin().getArenaTeam().hasPlayer(event.getPlayer().getUniqueId()))
        {
            event.setCancelled(true);
        }
        
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {      
            if(event.getItem() != null)
            {
                if(!UHC.getPlugin().getArenaTeam().isGameStarted())
                {         
                    event.setCancelled(true);
                    
                    if(event.getItem().getType() == Material.WOOD_DOOR)
                        GameAPI.kickPlayer(event.getPlayer());
                    
                    else if(event.getItem().getType() == Material.NETHER_STAR)
                        UHC.getPlugin().openGui(event.getPlayer(), new GuiSelectTeam());
                }
                else
                {
                    if(!UHC.getPlugin().getArenaTeam().hasPlayer(event.getPlayer().getUniqueId()))
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
