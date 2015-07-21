package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class UHCWorldInitEvent implements Listener
{
    @EventHandler
    public void event(WorldInitEvent event)
    {
        if(event.getWorld().getEnvironment() == World.Environment.NORMAL)
            UHC.getPlugin().finishInit();
    }
}
