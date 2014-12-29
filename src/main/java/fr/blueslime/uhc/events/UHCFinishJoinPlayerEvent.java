package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import net.samagames.gameapi.events.FinishJoinPlayerEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UHCFinishJoinPlayerEvent implements Listener
{
    @EventHandler
    public void event(FinishJoinPlayerEvent event)
    {
        if(UHC.getPlugin().getArena().getArenaPlayers().size() >= UHC.getPlugin().getArena().getTotalMaxPlayers())
        {
            event.refuse(ChatColor.RED + "L'arène est pleine.");
            return;
        }
        
        UHC.getPlugin().getArena().joinPlayer(event.getPlayer());
    }
}
