package fr.blueslime.uhc.arena.solo;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaGameSolo;
import fr.blueslime.uhc.arena.ArenaPlayer;
import net.samagames.gameapi.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class EndTimer extends Thread
{
    private final ArenaGameSolo parent;
    private long time;
    private boolean cont = true;

    public EndTimer(ArenaGameSolo parent)
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
        while (cont)
        {
            try
            {
                sleep(1000);
                time--;
                formatTime();
                setTimeout((int) time);

                if (parent.getActualPlayers() < parent.getMinPlayers())
                {
                    setTimeout(0);
                    this.end();
                    
                    return;
                }
                else if (time == 0)
                {
                    setTimeout(0);
                    this.end();
                    
                    Bukkit.getScheduler().runTask(UHC.getPlugin(), new Runnable()
                    {
                        public void run()
                        {
                            parent.finish(null);
                        }
                    });
                    return;
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
                time = mins + (mins == 1 ? " minute" : " minutes") + " et " + secs + (secs == 1 ? " seconde" : " secondes");
            }
            
            if (mins == 0)
            {
                if (secs == 30 || secs == 20 || secs == 10 || (secs <= 5 && secs > 0))
                {
                    time = secs + (secs == 1 ? " seconde" : " secondes");
                }
            }
        }
        if (time != null) {
            GameUtils.broadcastMessage(Messages.endOfGameIn.replace("${TIME}", time));
        }
    }

    public void setTimeout(int seconds)
    {
        boolean ring = false;
        
        if (seconds <= 5 && seconds != 0)
        {
            ring = true;
        }
        
        for(ArenaPlayer aPlayer : parent.getArenaPlayers())
        {
            aPlayer.getPlayer().getPlayer().setLevel(seconds);
            
            if (ring)
            {
                aPlayer.getPlayer().getPlayer().playSound(aPlayer.getPlayer().getPlayer().getLocation(), Sound.NOTE_PIANO, 1, 1);
            }
            if (seconds == 0)
            {
                aPlayer.getPlayer().getPlayer().playSound(aPlayer.getPlayer().getPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }
    }
}
