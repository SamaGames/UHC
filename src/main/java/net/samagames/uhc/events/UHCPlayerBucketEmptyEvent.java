package net.samagames.uhc.events;

import net.samagames.uhc.Messages;
import net.samagames.uhc.UHC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class UHCPlayerBucketEmptyEvent implements Listener
{
    @EventHandler
    public void event(PlayerBucketEmptyEvent event)
    {
        if (!UHC.getPlugin().getArena().isGameStarted())
        {
            event.setCancelled(true);
        }
        else if (!UHC.getPlugin().getArena().hasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else
        {            
            if(inRangeOfPortal(event.getBlockClicked().getLocation()))
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Messages.lavaPlaceRefused.toString());
            }
        }
    }

    private boolean inRangeOfPortal(Location loc)
    {
        int radius = 3;
        int offSetX = loc.getBlockX();
        int offSetZ = loc.getBlockZ();
        int offSetY = loc.getBlockY();

        World world = loc.getWorld();

        int startX = offSetX - radius;
        int startY = offSetY - radius;
        int startZ = offSetZ - radius;

        int endX = offSetX + radius;
        int endY = offSetY + radius;
        int endZ = offSetZ + radius;

        for (int counterX = startX; counterX < endX; counterX++)
        {
            for (int counterY = startY; counterY < endY; counterY++)
            {
                for (int counterZ = startZ; counterZ < endZ; counterZ++)
                {
                    Block block = world.getBlockAt(counterX, counterY, counterZ);
                    
                    if (block.getType() == Material.PORTAL)
                    {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
