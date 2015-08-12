package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import net.samagames.api.gui.AbstractGui;
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
                AbstractGui gui = UHC.getPlugin().getPlayerGui(player.getUniqueId());

                if (gui != null)
                {
                    String action = gui.getAction(event.getSlot());

                    if (action != null)
                        gui.onClick(player, event.getCurrentItem(), action, event.getClick());

                    event.setCancelled(true);
                }
            }
        }
    }
}
