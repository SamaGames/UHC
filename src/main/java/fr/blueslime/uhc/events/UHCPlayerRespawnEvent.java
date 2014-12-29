package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class UHCPlayerRespawnEvent implements Listener
{
    @EventHandler
    public void event(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        Arena arena = UHC.getPlugin().getArena();
        arena.loseRespawn(player);
    }
}
