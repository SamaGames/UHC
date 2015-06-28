package fr.blueslime.uhc.arena;

import fr.blueslime.uhc.UHC;
import net.samagames.core.api.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class WorldGenerator
{
    public BukkitTask task;
    public long lastShow;
    public int numberChunk;

    public WorldGenerator()
    {
        this.numberChunk = 0;
    }
    
    public void begin(final Game arena, final World world)
    {
        this.task = Bukkit.getScheduler().runTaskTimer(UHC.getPlugin(), new Runnable()
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
                    
                    if(System.currentTimeMillis() - lastShow > 1000L)
                    {
                        lastShow = System.currentTimeMillis();
                        Bukkit.getLogger().info("[UHC] Generating map... (" + (numberChunk * 100 / 15625) + "% finished)");
                    }
                    
                    z+=16;
                    
                    if (z >= 1000)
                    {
                        z = -1000;
                        x += 16;
                    }

                    if (x >= 1000)
                    {
                        finish();
                        break;
                    }
                    
                    i++;
                    numberChunk++;
                }
            }
        }, 1L, 1L);
    }

    private void finish()
    {
        this.task.cancel();
        UHC.getPlugin().finishGeneration();
    }
}