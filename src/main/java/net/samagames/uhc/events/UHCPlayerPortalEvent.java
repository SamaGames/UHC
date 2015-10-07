package net.samagames.uhc.events;

import eu.carrade.amaury.SafePortals.SafePortalsUtils;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class UHCPlayerPortalEvent implements Listener
{
    @EventHandler
    public void event(PlayerPortalEvent event)
    {
        TravelAgent travelAgent = event.getPortalTravelAgent();
        Location destination = travelAgent.findPortal(event.getTo());

        if(!SafePortalsUtils.isInsideBorder(destination))
        {
            event.useTravelAgent(false);
            boolean success = travelAgent.createPortal(event.getTo());

            if(success)
            {
                event.setTo(travelAgent.findPortal(event.getTo()));

                if (!SafePortalsUtils.isSafeSpot(event.getTo()))
                {
                    Location safeTo = SafePortalsUtils.searchSafeSpot(event.getTo());

                    if (safeTo != null)
                        event.setTo(safeTo);
                }
            }
        }
    }
}
