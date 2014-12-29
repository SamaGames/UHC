package fr.blueslime.uhc.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Gui
{
    public void display(Player player);
    public void onClic(Player player, ItemStack stack);
    public Inventory getInventory();
}
