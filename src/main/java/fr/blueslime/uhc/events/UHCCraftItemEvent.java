package fr.blueslime.uhc.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class UHCCraftItemEvent implements Listener
{
    @EventHandler
    public void event(CraftItemEvent event)
    {
        if (event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE && event.getRecipe().getResult().getDurability() == 1)
            event.getInventory().setResult(new ItemStack(Material.AIR));

        if(event.getRecipe().getResult().getType() == Material.SPECKLED_MELON)
        {
            if(event.getInventory().contains(Material.GOLD_NUGGET))
            {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(ChatColor.RED + "Ce craft est désactivé !");
            }
        }
    }
}
