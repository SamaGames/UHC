package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.gui.GuiSelectTeam;
import net.samagames.api.SamaGamesAPI;
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
        if(!UHC.getPlugin().getArena().hasPlayer(event.getPlayer()) || !UHC.getPlugin().getArena().isGameStarted())
        {
            event.setCancelled(true);
        }
        
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {      
            if(event.getItem() != null)
            {
                if(event.getItem().getType() == Material.WOOD_DOOR)
                    SamaGamesAPI.get().getGameManager().kickPlayer(event.getPlayer(), null);

                if(!UHC.getPlugin().getArena().isGameStarted())
                {         
                    event.setCancelled(true);

                    if(event.getItem().getType() == Material.NETHER_STAR)
                        UHC.getPlugin().openGui(event.getPlayer(), new GuiSelectTeam());
                }
                else
                {
                    if(!UHC.getPlugin().getArena().hasPlayer(event.getPlayer()))
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
