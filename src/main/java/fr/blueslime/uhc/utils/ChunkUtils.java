package fr.blueslime.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkUtils
{
    public static void generateChunk(World w, Chunk c)
    {
        int x = c.getX() - Bukkit.getViewDistance();
        int toX = x + (Bukkit.getViewDistance() * 2);
        int toZ = x + (Bukkit.getViewDistance() * 2);
        
        while (x < toX)
        {
            int z = c.getZ() - Bukkit.getViewDistance();
            
            while (z < toZ)
            {
                w.loadChunk(x, z, true);
                z++;
                
                Bukkit.getLogger().info("[UHC-Chunk] Generated chunk at x=" + x + " z=" + z);
            }
            
            x++;
        }
    }
}
