package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon.ArenaType;
import fr.blueslime.uhc.arena.ArenaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UHCAsyncPlayerChatEvent implements Listener
{
    @EventHandler
    public void event(AsyncPlayerChatEvent event)
    {
        if(UHC.getPlugin().getArena().getArenaType() == ArenaType.TEAM)
        {
            if(UHC.getPlugin().getArena().isGameStarted())
            {
                if(UHC.getPlugin().getArena().hasPlayer(event.getPlayer().getUniqueId()))
                {
                    event.setCancelled(true);

                    ArenaPlayer player = UHC.getPlugin().getArena().getPlayer(event.getPlayer().getUniqueId());
                    player.getTeam().chat(player, event.getMessage());
                }
            }
        }
    }
}
