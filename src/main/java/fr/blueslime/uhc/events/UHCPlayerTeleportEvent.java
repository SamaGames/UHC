package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class UHCPlayerTeleportEvent implements Listener
{
    @EventHandler
    public void event(PlayerTeleportEvent event)
    {
        if(event.getCause() == TeleportCause.NETHER_PORTAL)
        {
            if(event.getTo().getWorld().getEnvironment() == Environment.NORMAL)
            {
                ArenaCommon arena = UHC.getPlugin().getArena();
                Location loc = event.getTo();
                
                if(
                    loc.getX() > arena.getWorldBorder().getSize() ||
                    loc.getX() < -arena.getWorldBorder().getSize() ||
                    loc.getZ() > arena.getWorldBorder().getSize() ||
                    loc.getZ() < -arena.getWorldBorder().getSize()
                )
                {
                    int x = new Random().nextInt(200) - 100;
                    int y = new Random().nextInt(200) - 100;
                    
                    event.getPlayer().teleport(new Location(arena.getWorld(), x, arena.getWorld().getHighestBlockYAt(x, y), y));
                }
            }
        }
    }
}
