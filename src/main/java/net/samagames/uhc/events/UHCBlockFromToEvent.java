package net.samagames.uhc.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class UHCBlockFromToEvent implements Listener
{
    @EventHandler
    public void event(BlockFromToEvent event)
    {
        if(event.getBlock().getType() == Material.STATIONARY_LAVA || event.getBlock().getType() == Material.LAVA)
        {
            if(inRangeOfPortal(event.getBlock().getLocation()))
            {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
            }
        }
    }
    
    private boolean inRangeOfPortal(Location loc)
    {
        int radius = 5;
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
