package net.samagames.uhc.events;

import net.samagames.api.games.Status;
import net.samagames.uhc.UHC;
import net.samagames.uhc.arena.ArenaCommon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class UHCPlayerMoveEvent implements Listener
{
    @EventHandler
    public void event(PlayerMoveEvent event)
    {
        ArenaCommon game = UHC.getPlugin().getArena();

        if ((game.getStatus() == Status.STARTING || game.getStatus() == Status.WAITING_FOR_PLAYERS) && event.getTo().getY() < 125)
        {
            event.setCancelled(true);
            event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 200, 0.5));
            event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Mais où vous allez comme ça ?!");
        }
    }
}
