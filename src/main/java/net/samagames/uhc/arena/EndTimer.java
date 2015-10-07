package net.samagames.uhc.arena;

import net.samagames.api.SamaGamesAPI;
import net.samagames.uhc.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EndTimer extends Thread
{
    private final ArenaCommon parent;
    private long time;
    private boolean cont = true;

    public EndTimer(ArenaCommon parent)
    {
        this.parent = parent;
        this.time = 60;
    }

    public void end()
    {
        this.cont = false;
    }

    public void run()
    {
        while (this.cont)
        {
            try
            {
                sleep(1000);

                this.time--;
                this.formatTime();
                this.setTimeout((int) time);

                if (parent.getInGamePlayers().size() < SamaGamesAPI.get().getGameManager().getGameProperties().getMinSlots())
                {
                    this.setTimeout(0);
                    this.end();
                    
                    return;
                }
                else if (time == 0)
                {
                    this.setTimeout(0);
                    this.end();
                    
                    if(parent.getArenaType() == ArenaCommon.ArenaType.TEAM)
                        parent.finishTeam(null);
                    else
                        parent.finishSolo(null);
                }

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void formatTime()
    {
        int hours = (int) time / 3600;
        int remainder = (int) time - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        String time = null;
        
        if (hours > 1)
        {
            if (secs == 0)
            {
                if (mins == 30 || mins == 0)
                {
                    time = hours + "h" + mins;
                }
            }
        }
        else
        {
            if ((mins == 45 || mins == 30 || mins == 20 || mins == 10 || mins == 5 || mins == 3 || mins == 2 || mins == 1) && secs == 0)
            {
                time = mins + (mins == 1 ? " minute" : " minutes");
            }
            
            if (mins == 1 && secs == 30)
            {
                time = mins + " minute" + " et " + secs + " secondes";
            }
            
            if (mins == 0)
            {
                if (secs == 30 || secs == 20 || secs == 10 || (secs <= 5 && secs > 0))
                {
                    time = secs + (secs == 1 ? " seconde" : " secondes");
                }
            }
        }

        if (time != null)
            Bukkit.broadcastMessage(Messages.endOfGameIn.toString().replace("${TIME}", time));
    }

    public void setTimeout(int seconds)
    {
        boolean ring = false;
        
        if (seconds <= 5 && seconds != 0)
            ring = true;
        
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if (ring)
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);

            if (seconds == 0)
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        }
    }
}
