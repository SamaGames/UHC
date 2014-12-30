package fr.blueslime.uhc.events.common;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class UHCEntityDeathEvent implements Listener
{
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Ghast)
        {
            List<ItemStack> drops = new ArrayList<>(event.getDrops());
            event.getDrops().clear();
            
            for (ItemStack i : drops)
            {
                if (i.getType() == Material.GHAST_TEAR)
                {
                    event.getDrops().add(new ItemStack(Material.SULPHUR, i.getAmount()));
                }
                else
                {
                    event.getDrops().add(i);
                }
                
                event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 1));
            }
        }
    }
}
