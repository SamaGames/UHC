package fr.blueslime.uhc.commands;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaPlayer;
import fr.blueslime.uhc.arena.ArenaTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJoin
{
    public static boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs instanceof Player)
        {
            if(!UHC.getPlugin().getArenaTeam().isGameStarted())
            {
                String team = strings[1];
                String player = strings[2];
                ArenaPlayer aPlayer = UHC.getPlugin().getArenaTeam().getPlayer(Bukkit.getPlayer(player).getUniqueId());
                ArenaTeam aTeam = UHC.getPlugin().getArenaTeam().getTeamByNumber(Integer.valueOf(team));
                
                if(!aPlayer.hasTeam())
                {
                    if(aTeam.isInvited(aPlayer.getPlayerID()))
                    {
                        aTeam.join(aPlayer);
                        aPlayer.getPlayer().sendMessage(Messages.teamJoined.replace("${TEAM}", aTeam.getChatColor() + aTeam.getName()) + ChatColor.YELLOW);
                    }
                }
            }
        }
        
        return true;
    }
}
