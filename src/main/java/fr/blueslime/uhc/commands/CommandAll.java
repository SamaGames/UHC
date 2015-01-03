package fr.blueslime.uhc.commands;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon.ArenaType;
import fr.blueslime.uhc.arena.ArenaPlayer;
import java.util.Date;
import net.zyuiop.MasterBundle.MasterBundle;
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
            
            if(UHC.getPlugin().getArena().getArenaType() == ArenaType.TEAM)
            {
                if(UHC.getPlugin().getArena().isGameStarted() && UHC.getPlugin().getArena().hasPlayer(sender.getUniqueId()))
                {
                    if(strings.length != 0)
                    {
                        Player player = (Player) cs;

                        if (MasterBundle.plugin.mutedPlayers.containsKey(player.getUniqueId()))
                        {
                            Date end = MasterBundle.plugin.mutedPlayers.get(player.getUniqueId());

                            if (end.before(new Date()))
                            {
                                MasterBundle.plugin.mutedPlayers.remove(player.getUniqueId());
                                MasterBundle.plugin.muteReasons.remove(player.getUniqueId());
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED + "Vous êtes actuellement muet pour une durée de " + formatTime(end));
                                player.sendMessage(ChatColor.RED + "Raison : " + ChatColor.YELLOW + MasterBundle.plugin.muteReasons.get(player.getUniqueId()));
                                return true;
                            }
                        }

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
            else
            {
                sender.sendMessage(Messages.wrongGameType);
            }
        }
        
        return true;
    }
    
    public String formatTime(Date end)
    {
        long time = (end.getTime() - new Date().getTime()) / 1000;
        int days = (int) time / (3600 * 24);
        int remainder = (int) time - days * (3600 * 24);
        int hours = remainder / 3600;
        remainder = remainder - (hours * 3600);
        int mins = remainder / 60;

        Bukkit.getLogger().info("INFO TIME : " + time + " - " + days + " - " + remainder + " - " + hours + " - " + mins);

        String ret = "";
        if (days > 0) {
            ret += ChatColor.YELLOW + "" + days + " jours ";
        }

        if (hours > 0) {
            ret += ChatColor.YELLOW + "" + hours + " heures ";
        }

        if (mins > 0) {
            ret += ChatColor.YELLOW + "" + mins + " minutes ";
        }

        if (ret.equals("") && mins == 0)
            ret += ChatColor.YELLOW + "" + "Moins d'une minute";

        return ret;
    }
}
