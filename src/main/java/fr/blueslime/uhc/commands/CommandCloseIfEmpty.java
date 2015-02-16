package fr.blueslime.uhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandCloseIfEmpty implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(Bukkit.getOnlinePlayers().isEmpty())
            Bukkit.shutdown();

        cs.sendMessage("lel");

        return true;
    }
}
