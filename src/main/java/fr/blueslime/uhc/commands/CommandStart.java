package fr.blueslime.uhc.commands;

import fr.blueslime.uhc.UHC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandStart
{
    public static boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(UHC.getPlugin().getArenaSolo() != null)
        {
            if(!UHC.getPlugin().getArenaSolo().isGameStarted())
                UHC.getPlugin().getArenaSolo().startGame();
        }
        else
        {
            if(!UHC.getPlugin().getArenaTeam().isGameStarted())
                UHC.getPlugin().getArenaTeam().startGame();   
        }
                
        return true;
    }
}
