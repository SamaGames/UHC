package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import net.samagames.gameapi.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.samagames.gameapi.events.RejoinPlayerEvent;

public class UHCRejoinPlayerEvent implements Listener
{
    @EventHandler
    public void event(RejoinPlayerEvent event)
    {
        GameAPI.removeRejoinList(event.getPlayer());
        UHC.getPlugin().getArena().playerReconnected(event.getPlayer());
    }
}
