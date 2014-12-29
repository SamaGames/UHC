package fr.blueslime.uhc.events;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class UHCCraftItemEvent implements Listener
{
    @EventHandler
    public void event(CraftItemEvent event)
    {
        try {
            if (event.getRecipe() instanceof ShapedRecipe)
            {
                ShapedRecipe r = (ShapedRecipe) event.getRecipe();
                
                for (Map.Entry<Character, ItemStack> e : r.getIngredientMap().entrySet())
                {
                    if (r.getResult().getType() == Material.GOLDEN_APPLE && e != null && e.getValue() != null && e.getValue().getType() == Material.GOLD_NUGGET)
                    {
                        event.setCancelled(true);
                    }
                }
                
                if (event.isCancelled())
                {
                    ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Vous ne pouvez pas crafter la pomme d'or comme ceci");
                }
            }
            else if (event.getRecipe() instanceof ShapelessRecipe)
            {
                ShapelessRecipe r = (ShapelessRecipe) event.getRecipe();
                
                for (ItemStack i : r.getIngredientList())
                {
                    if (i.getType() == Material.GOLD_NUGGET && r.getResult().getType() == Material.SPECKLED_MELON)
                    {
                        event.setCancelled(true);
                    }
                }
                
                if (event.isCancelled())
                {
                    ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Vous ne pouvez pas crafter le melon scintillant comme ceci");
                }
            }
        }
        catch (Exception e) {}
    }
}
