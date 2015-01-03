package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class UHCInventoryClickEvent implements Listener
{
    @EventHandler
    public void event(InventoryClickEvent event)
    {
        if(event.getWhoClicked() instanceof Player)
        {
            Player player = (Player) event.getWhoClicked();
            
            if(!UHC.getPlugin().getArena().isGameStarted() && event.getView().getType() == InventoryType.PLAYER)
            {
                event.setCancelled(true);
            }
            
            if(event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null)
            {
                if(UHC.getPlugin().getPlayerGui(player.getUniqueId()) != null)
                {
                    UHC.getPlugin().getPlayerGui(player.getUniqueId()).onClic(player, event.getCurrentItem());
                    event.setCancelled(true);
                }
            }
        }
    }
}
