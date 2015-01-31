package fr.blueslime.uhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandInv implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs instanceof Player)
        {
            if(strings.length != 0)
            {
                String playerName = strings[0];
                Player player = Bukkit.getPlayer(playerName);
                
                if(player != null)
                {
                    Inventory inventory = player.getInventory();
                    ((Player) cs).closeInventory();
                    ((Player) cs).openInventory(inventory);
                }
                else
                {
                    cs.sendMessage(ChatColor.RED + "Le joueur spécifié n'est pas en ligne !");
                }
            }
            else
            {
                return false;
            }
        }
        
        return true;
    }
}
