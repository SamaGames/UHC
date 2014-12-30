package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.events.common.*;
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
        UHC.getPlugin().getArenaSolo().playerReconnected(event.getPlayer());
    }
}
