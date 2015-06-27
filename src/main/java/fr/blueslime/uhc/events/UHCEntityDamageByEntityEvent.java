package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon.ArenaType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class UHCEntityDamageByEntityEvent implements Listener
{
    @EventHandler
    public void event(EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() == EntityType.PLAYER)
        {
            if(!UHC.getPlugin().getArena().hasPlayer((Player) event.getDamager()))
                event.setCancelled(true);
            else if(UHC.getPlugin().getArena().isSpectator((Player) event.getDamager()))
                event.setCancelled(true);
        }
        
        if(event.getEntity().getType() == EntityType.PLAYER)
        {
            Player damaged = (Player) event.getEntity();
            
            if(!UHC.getPlugin().getArena().isFirstMinute())
            {
                event.setCancelled(true);
            }
            
            if(event.getDamager().getType() == EntityType.PLAYER)
            {
                if(!UHC.getPlugin().getArena().hasPlayer((Player) event.getDamager()))
                    event.setCancelled(true);
                else if(UHC.getPlugin().getArena().isSpectator((Player) event.getDamager()))
                    event.setCancelled(true);
                else if(UHC.getPlugin().getArena().getArenaType() == ArenaType.TEAM && UHC.getPlugin().getArena().getPlayer(damaged.getUniqueId()).hasTeam() && UHC.getPlugin().getArena().getPlayer(damaged.getUniqueId()).getTeam().hasPlayer(event.getDamager().getUniqueId()))
                    event.setCancelled(true);
            }
            else if(event.getDamager().getType() == EntityType.ARROW)
            {
                Arrow arrow = (Arrow) event.getDamager();
                
                if(arrow.getShooter() instanceof Player)
                {
                    Player shooter = (Player) arrow.getShooter();
                    
                    if(shooter.getUniqueId().equals(damaged.getUniqueId()))
                        event.setCancelled(true);
                    else if(UHC.getPlugin().getArena().getArenaType() == ArenaType.TEAM && UHC.getPlugin().getArena().getPlayer(damaged.getUniqueId()).hasTeam() && UHC.getPlugin().getArena().getPlayer(damaged.getUniqueId()).getTeam().hasPlayer(shooter.getUniqueId()))
                        event.setCancelled(true);
                }
            }
        }
    }
}
