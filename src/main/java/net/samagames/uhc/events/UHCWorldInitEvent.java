package net.samagames.uhc.events;

import net.samagames.uhc.UHC;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class UHCWorldInitEvent implements Listener
{
    @EventHandler
    public void event(WorldInitEvent event)
    {
        if(event.getWorld().getEnvironment() == World.Environment.THE_END)
            UHC.getPlugin().finishInit();
    }
}
