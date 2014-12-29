package fr.blueslime.uhc.events;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaPlayer;
import net.samagames.gameapi.events.JoinModEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UHCJoinModEvent implements Listener
{
    @EventHandler
    public void event(JoinModEvent event)
    {
        Bukkit.getPlayer(event.getPlayer()).sendMessage(Messages.PLUGIN_TAG + ChatColor.RED + "Hey! Tu es incognito dans le jeu donc fait gaffe ;) Essai pas de tricher, j'ai tout prévu.");
        Bukkit.getPlayer(event.getPlayer()).setScoreboard(UHC.getPlugin().getArena().getScoreboard());
        Bukkit.getPlayer(event.getPlayer()).getInventory().clear();
        
        for(ArenaPlayer player : UHC.getPlugin().getArena().getArenaPlayers())
        {
            player.getPlayer().hidePlayer(Bukkit.getPlayer(event.getPlayer()));
        }
    }
}
