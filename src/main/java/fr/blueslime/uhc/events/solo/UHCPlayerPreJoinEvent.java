package fr.blueslime.uhc.events.solo;

import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.UHC;
import net.samagames.gameapi.events.PreJoinPlayerEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UHCPlayerPreJoinEvent implements Listener
{
    @EventHandler
    public void event(PreJoinPlayerEvent event)
    {        
        if(!UHC.getPlugin().canJoin())
        {
            event.refuse(ChatColor.RED + "Le jeu n'est pas encore prêt :'(");
        }
        
        if(UHC.getPlugin().getArenaSolo().getArenaPlayers().size() >= UHC.getPlugin().getArenaSolo().getTotalMaxPlayers())
        {
            event.refuse(ChatColor.RED + "L'arène est pleine.");
        }
    }
}
