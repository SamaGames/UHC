package fr.blueslime.uhc.arena;

import fr.blueslime.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class WorldGenerator
{
    public static BukkitTask task;

    public static void begin(final World world)
    {
        task = Bukkit.getScheduler().runTaskTimer(UHC.getPlugin(), new Runnable()
        {
            private int x = -1000;
            private int z = -1000;

            @Override
            public void run()
            {
                int i = 0;
                
                while (i < 5)
                {
                    world.getChunkAt(world.getBlockAt(x, 64, z)).load(true);
                    Bukkit.getLogger().info("Loaded chunk at : " + x + ", " + z + ".");
                    z+=16;
                    
                    if (z >= 1000)
                    {
                        z = - 1000;
                        x += 16;
                    }

                    if (x >= 1000)
                    {
                        WorldGenerator.finish();
                        break;
                    }
                    
                    i++;
                }
            }
        }, 1L, 1L);
    }

    private static void finish()
    {
        task.cancel();
        UHC.getPlugin().finishGeneration();
    }
}