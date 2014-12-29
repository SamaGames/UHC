package fr.blueslime.uhc.arena;

import java.util.UUID;
import org.bukkit.entity.Player;

public class ArenaPlayer
{
    private final Player player;
    private final UUID playerId;
    
    private ArenaTeam team;
    
    public ArenaPlayer(Player player)
    {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.team = null;
        
        player.getInventory().clear();
    }
    
    public void setTeam(ArenaTeam team)
    {
        this.team = team;
    }

    public Player getPlayer()
    {
        return this.player;
    }
    
    public UUID getPlayerID()
    {
        return this.playerId;
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
