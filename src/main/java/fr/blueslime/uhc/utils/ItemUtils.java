package fr.blueslime.uhc.utils;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemUtils
{
    public static ItemStack createDisplay(Material material, int quantity, String name, ArrayList<String> lores, boolean magical)
    {
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        
        if(lores != null)
            meta.setLore(lores);
        
        if(magical)
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
        
        item.setItemMeta(meta);
        
        return item;
    }
    
    public static ItemStack createDisplay(Material material, int quantity, int datatag, String name, ArrayList<String> lores, boolean magical)
    {
        ItemStack item = new ItemStack(material, quantity, (short) datatag);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        
        if(lores != null)
            meta.setLore(lores);
        
        if(magical)
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
        
        item.setItemMeta(meta);
        
        return item;
    }
    
    public static ItemStack createDisplay(Material material, Inventory inv, int quantity, int slot, String name, ArrayList<String> lores, boolean magical)
    {
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if(lores != null)
        {
            ArrayList<String> newList = new ArrayList<>();
            
            for(String lore : lores)
            {
                newList.add(ChatColor.RESET + "" + ChatColor.GRAY + lore);
            }
            
            meta.setLore(newList);
        }
        
        if(magical)
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
        
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        
        return item;
    }
    
    public static ItemStack createDisplay(ItemStack item, Inventory inv, int slot, String name, ArrayList<String> lores, boolean magical)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if(lores != null)
        {
            ArrayList<String> newList = new ArrayList<>();
            
            for(String lore : lores)
            {
                newList.add(ChatColor.RESET + "" + ChatColor.GRAY + lore);
            }
            
            meta.setLore(newList);
        }
        
        if(magical)
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
        
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        
        return item;
    }
    
    public static ItemStack createDisplay(Material material, Inventory inv, int quantity, int datatag, int slot, String name, ArrayList<String> lores, boolean magical)
    {
        ItemStack item = new ItemStack(material, quantity, (short) datatag);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if(lores != null)
        {
            ArrayList<String> newList = new ArrayList<>();
            
            for(String lore : lores)
            {
                newList.add(ChatColor.RESET + "" + ChatColor.GRAY + lore);
            }
            
            meta.setLore(newList);
        }
        
        if(magical)
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
        
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        
        return item;
    }
    
    public static ItemStack setColor(ItemStack item, int red, int green, int blue)
    {
        LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
        lam.setColor(Color.fromRGB(red, green, blue));
        item.setItemMeta(lam);
        
        return item;
    }
    
    public static boolean isArmor(ItemStack stack)
    {
        if(stack.getType() == Material.LEATHER_HELMET || stack.getType() == Material.LEATHER_CHESTPLATE || stack.getType() == Material.LEATHER_LEGGINGS || stack.getType() == Material.LEATHER_BOOTS)
            return true;
        else if(stack.getType() == Material.IRON_HELMET || stack.getType() == Material.IRON_CHESTPLATE || stack.getType() == Material.IRON_LEGGINGS || stack.getType() == Material.IRON_BOOTS)
            return true;
        else if(stack.getType() == Material.CHAINMAIL_HELMET || stack.getType() == Material.CHAINMAIL_CHESTPLATE || stack.getType() == Material.CHAINMAIL_LEGGINGS || stack.getType() == Material.CHAINMAIL_BOOTS)
            return true;
        else if(stack.getType() == Material.GOLD_HELMET || stack.getType() == Material.GOLD_CHESTPLATE || stack.getType() == Material.GOLD_LEGGINGS || stack.getType() == Material.GOLD_BOOTS)
            return true;
        else if(stack.getType() == Material.DIAMOND_HELMET || stack.getType() == Material.DIAMOND_CHESTPLATE || stack.getType() == Material.DIAMOND_LEGGINGS || stack.getType() == Material.DIAMOND_BOOTS)
            return true;
        else
            return false;
    }
    
    public static boolean isSword(ItemStack stack)
    {
        if(stack.getType() == Material.WOOD_SWORD || stack.getType() == Material.STONE_SWORD || stack.getType() == Material.IRON_SWORD || stack.getType() == Material.GOLD_SWORD || stack.getType() == Material.DIAMOND_SWORD)
            return true;
        else
            return false;
    }
}

