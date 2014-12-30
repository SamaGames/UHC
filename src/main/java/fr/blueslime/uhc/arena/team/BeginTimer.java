package fr.blueslime.uhc.arena.team;

import fr.blueslime.uhc.arena.ArenaGameTeam;
import fr.blueslime.uhc.arena.ArenaPlayer;
import net.samagames.gameapi.GameUtils;
import net.samagames.gameapi.json.Status;
import net.samagames.gameapi.themachine.messages.StaticMessages;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BeginTimer implements Runnable
{
    private final ArenaGameTeam parent;
    private long time;
    private boolean ready;

    public BeginTimer(ArenaGameTeam parent)
    {
        this.parent = parent;
        this.time = 120;
        this.ready = false;
    }
 
    @Override
    public void run()
    {
        int nPlayers = this.parent.countGamePlayers();
 
        if (nPlayers >= this.parent.getMinPlayers() && !this.ready)
        {
            this.ready = true;
            this.parent.setStatus(Status.Starting);
            this.time = 120;
        }

        if (nPlayers < this.parent.getMinPlayers() && this.ready)
        {
            this.ready = false;
            this.parent.setStatus(Status.Available);
            
            GameUtils.broadcastMessage(StaticMessages.NOTENOUGTHPLAYERS.get(this.parent.getCoherenceMachine()));
            
            for (Player p : Bukkit.getOnlinePlayers())
                p.setLevel(120);
        }

        if (this.ready)
        {
            this.time--;
            formatTime();
            setTimeout((int) this.time);
            
            if(this.time == 0)
            {
                this.parent.startGame();
            }
        }
    }

    public void formatTime()
    {
        int hours = (int) this.time / 3600;
        int remainder = (int) this.time - hours * 3600;
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
            GameUtils.broadcastMessage(StaticMessages.STARTIN.get(this.parent.getCoherenceMachine()).replace("${TIME}", time));
        }
    }

    public void setTimeout(int seconds)
    {
        boolean ring = false;
        
        if (seconds <= 5 && seconds != 0)
        {
            ring = true;
        }
        
        for(ArenaPlayer aPlayer : this.parent.getArenaPlayers())
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
