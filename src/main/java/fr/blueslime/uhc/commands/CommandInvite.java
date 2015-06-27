package fr.blueslime.uhc.commands;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon.ArenaType;
import fr.blueslime.uhc.arena.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvite
{
    public static boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs instanceof Player)
        {
            if(UHC.getPlugin().getArena().getArenaType() == ArenaType.TEAM)
            {
                if(!UHC.getPlugin().getArena().isGameStarted())
                {
                    String inviter = strings[1];
                    String invited = strings[2];
                    ArenaPlayer aInviter = UHC.getPlugin().getArena().getPlayer(Bukkit.getPlayer(inviter).getUniqueId());
                    ArenaPlayer aInvited = UHC.getPlugin().getArena().getPlayer(Bukkit.getPlayer(invited).getUniqueId());

                    if(aInviter.hasTeam() && !aInviter.getTeam().isFull())
                    {
                        if(aInviter.getTeam().isInvited(aInvited.getUUID()))
                            aInviter.getTeam().removeInvite(aInvited.getUUID());

                        aInviter.getTeam().invite(aInviter, aInvited);
                        aInviter.getPlayerIfOnline().sendMessage(Messages.inviteSuccessful.toString());
                    }
                    else
                    {
                        aInviter.getPlayerIfOnline().sendMessage(Messages.teamFull.toString());
                    }
                }
            }
        }
                
        return true;
    }
}
