package fr.blueslime.uhc.events.team;

import fr.blueslime.uhc.events.solo.*;
import fr.blueslime.uhc.events.common.*;
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
        if(UHC.getPlugin().getArenaTeam().getArenaPlayers().size() >= UHC.getPlugin().getArenaTeam().getTotalMaxPlayers())
        {
            event.refuse(ChatColor.RED + "L'arène est pleine.");
            return;
        }
        
        UHC.getPlugin().getArenaTeam().joinPlayer(event.getPlayer());
    }
}
