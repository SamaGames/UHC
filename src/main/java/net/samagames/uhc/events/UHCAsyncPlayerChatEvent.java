package net.samagames.uhc.events;

import net.samagames.uhc.UHC;
import net.samagames.uhc.arena.ArenaCommon;
import net.samagames.uhc.arena.ArenaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UHCAsyncPlayerChatEvent implements Listener
{
    @EventHandler
    public void event(AsyncPlayerChatEvent event)
    {
        if(UHC.getPlugin().getArena().getArenaType() == ArenaCommon.ArenaType.TEAM)
        {
            if(UHC.getPlugin().getArena().isGameStarted())
            {
                if(UHC.getPlugin().getArena().hasPlayer(event.getPlayer()))
                {
                    event.setCancelled(true);

                    ArenaPlayer player = UHC.getPlugin().getArena().getPlayer(event.getPlayer().getUniqueId());
                    player.getTeam().chat(player, event.getMessage());
                }
            }
        }
    }
}
