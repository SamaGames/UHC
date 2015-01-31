package fr.blueslime.uhc.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class UHCCraftItemEvent implements Listener
{
    @EventHandler
    public void event(CraftItemEvent event)
    {
        if(event.getRecipe().getResult().getType() == Material.SPECKLED_MELON || event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE)
        {
            if(event.getInventory().contains(Material.GOLD_NUGGET))
            {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Ce craft est désactivé !");
            }
        }
    }
}
