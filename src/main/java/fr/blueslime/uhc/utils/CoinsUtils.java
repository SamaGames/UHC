package fr.blueslime.uhc.utils;

public class CoinsUtils
{
    public static int getKillCoins(int episode)
    {
        if(episode == 1) return 0;
        else if(episode == 2) return 50;
        else if(episode == 3) return 250;
        else if(episode == 4) return 300;
        else if(episode == 5) return 350;
        else if(episode >= 6) return 400;
        else return 0;
    }
    
    public static int getWinCoins(int episode)
    {
        if(episode == 1) return 0;
        else if(episode == 2) return 100;
        else if(episode == 3) return 500;
        else if(episode == 4) return 600;
        else if(episode >= 5) return 800;
        else return 0;
    }
}
