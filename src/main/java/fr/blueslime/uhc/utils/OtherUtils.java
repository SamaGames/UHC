package fr.blueslime.uhc.utils;

import java.util.Random;
import org.bukkit.ChatColor;

public class OtherUtils
{
    public static String replaceColors(String message, boolean colors)
    {
        if (!colors)
        {
            return whiteColors(message);
        }

        String s = message;

        for (ChatColor color : ChatColor.values())
        {
            s = s.replaceAll("(?i)&" + color.getChar(), "" + color);
        }
        
        return s;
    }
    
    public static String whiteColors(String message)
    {
        String s = message;
        
        for (ChatColor color : ChatColor.values())
        {
            s = s.replaceAll("(?i)&" + color.getChar(), "");
        }
        
        return s;

    }
    
     public static int randInt(int min, int max)
     {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
