package fr.blueslime.uhc.events.team;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UHCAsyncPlayerChatEvent implements Listener
{
    @EventHandler
    public void event(AsyncPlayerChatEvent event)
    {
        if(UHC.getPlugin().getArenaTeam().isGameStarted())
        {
            if(UHC.getPlugin().getArenaTeam().hasPlayer(event.getPlayer().getUniqueId()))
            {
                event.setCancelled(true);

                ArenaPlayer player = UHC.getPlugin().getArenaTeam().getPlayer(event.getPlayer().getUniqueId());
                player.getTeam().chat(player, event.getMessage());
            }
        }
    }
}
