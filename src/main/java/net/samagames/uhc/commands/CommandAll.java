package net.samagames.uhc.commands;

import net.samagames.uhc.UHC;
import net.samagames.uhc.arena.ArenaPlayer;
import net.samagames.uhc.arena.ArenaCommon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAll implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs instanceof Player)
        {
            Player sender = (Player) cs;
            
            if(UHC.getPlugin().getArena().getArenaType() == ArenaCommon.ArenaType.TEAM)
            {
                if(UHC.getPlugin().getArena().isGameStarted())
                {
                    if(strings.length != 0)
                    {
                        ArenaPlayer aPlayer = UHC.getPlugin().getArena().getPlayer(sender.getUniqueId());
                        StringBuilder messageBuilder = new StringBuilder();

                        messageBuilder.append(aPlayer.getTeam().getChatColor()).append("[").append(aPlayer.getTeam().getName()).append(aPlayer.getTeam().getChatColor()).append("]").append(" ");
                        messageBuilder.append(sender.getName()).append(ChatColor.RESET).append(": ");

                        for(String s : strings)
                        {
                            messageBuilder.append(s).append(" ");
                        }

                        String message = messageBuilder.toString();

                        for(Player pplayer : Bukkit.getOnlinePlayers())
                        {
                            pplayer.sendMessage(message);
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas envoyer un message vide :(");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Vous ne pouvez utiliser cette commande que quand vous êtes en jeu !");
                }
            }
        }
        
        return true;
    }
}
