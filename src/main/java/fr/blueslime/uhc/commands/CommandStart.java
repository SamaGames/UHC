package fr.blueslime.uhc.commands;

import fr.blueslime.uhc.UHC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandStart
{
    public static boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(UHC.getPlugin().getArena() != null)
        {
            if(!UHC.getPlugin().getArena().isGameStarted())
                UHC.getPlugin().getArena().startGame();
        }
                
        return true;
    }
}
