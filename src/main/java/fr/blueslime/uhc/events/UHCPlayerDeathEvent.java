package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class UHCPlayerDeathEvent implements Listener
{
    @EventHandler
    public void event(PlayerDeathEvent event)
    {
        final Player deadPlayer = event.getEntity();
        ArenaCommon arena = UHC.getPlugin().getArena();
                
        if(arena.isGameStarted())
        {
            if(arena.hasPlayer(deadPlayer))
            {
                if(deadPlayer.isDead())
                {
                    arena.lose(deadPlayer.getUniqueId());
                    event.getEntity().getLocation().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(Material.GOLDEN_APPLE, 1));
                }
            }
            else
            {
                event.setDeathMessage(null);
            }
        }
    }
}
