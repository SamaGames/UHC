package net.samagames.uhc.events;

import net.samagames.uhc.Messages;
import net.samagames.uhc.UHC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class UHCBlockPlaceEvent implements Listener
{
    @EventHandler
    public void event(BlockPlaceEvent event)
    {
        if(!UHC.getPlugin().getArena().isGameStarted())
        {
            event.setCancelled(true);
        }
        else if(!UHC.getPlugin().getArena().hasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else
        {      
            boolean flag = false;
            
            if(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(1.0D, 0.0D, 0.0D)).getType() == Material.PORTAL)
                flag = true;
            
            if(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(1.0D, 0.0D, 0.0D)).getType() == Material.PORTAL)
                flag = true;
            
            if(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0.0D, 0.0D, 1.0D)).getType() == Material.PORTAL)
                flag = true;
            
            if(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0D, 0.0D, 1.0D)).getType() == Material.PORTAL)
                flag = true;
            
            if(flag)
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Messages.blockPlaceRefused.toString());
            }
        }
    }
}
