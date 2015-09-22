package net.samagames.uhc.arena;

import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.Player;

public class ArenaPlayer extends GamePlayer
{
    private ArenaTeam team;
    
    public ArenaPlayer(Player player)
    {
        super(player);
        this.team = null;
    }

    @Override
    public void handleLogin(boolean reconnect)
    {
        if(!reconnect)
            this.getPlayerIfOnline().getInventory().clear();
    }
    
    public void setTeam(ArenaTeam team)
    {
        this.team = team;
    }

    public ArenaTeam getTeam()
    {
        return this.team;
    }
    
    public boolean hasTeam()
    {
        return this.team != null;
    }
}
