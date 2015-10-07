package net.samagames.uhc;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class UHCGenerator extends JavaPlugin
{
    private BukkitTask postInitTask;
    private int lastShow = -1;
    private int numberChunk;
    private int count;

    public void onEnable()
    {
        this.patchBiomes();
        this.postInitTask = Bukkit.getScheduler().runTaskTimer(this, this::postInit, 20L, 20L);
    }

    public void postInit()
    {
        if(Bukkit.getWorlds().size() == 2)
        {
            this.postInitTask.cancel();

            this.loadWorld(Bukkit.getWorlds().get(0));
            this.loadWorld(Bukkit.getWorlds().get(1));

            Bukkit.getScheduler().runTaskTimer(this, () ->
            {
                if(this.count == 2)
                    Bukkit.shutdown();
            }, 20L, 20L);
        }
    }

    public void loadWorld(org.bukkit.World world)
    {
        this.lastShow = -1;
        this.numberChunk = 0;

        new BukkitRunnable()
        {
            private int todo = (1200 * 1200) / 256;
            private int x = -600;
            private int z = -600;

            @Override
            public void run()
            {
                int i = 0;
                while (i < 50)
                {
                    world.getChunkAt(world.getBlockAt(x, 64, z)).load(true);
                    int percentage = numberChunk * 100 / todo;

                    if (percentage > lastShow && percentage % 10 == 0)
                    {
                        lastShow = percentage;
                        getLogger().info("Loading chunks of " + world.getName() + " (" + percentage + "%)");
                    }

                    z += 16;

                    if (z >= 600)
                    {
                        z = -600;
                        x += 16;
                    }

                    if (x >= 600)
                    {
                        count++;
                        this.cancel();
                        return;
                    }

                    numberChunk++;
                    i++;
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    public void patchBiomes()
    {
        BiomeBase[] a = BiomeBase.getBiomes();
        BiomeForest nb1 = new BiomeForest(0, 0);
        BiomeForest nb2 = new BiomeForest(24, 0);

        try
        {
            Method m1 = BiomeBase.class.getDeclaredMethod("a", int.class, boolean.class);
            m1.setAccessible(true);

            Method m2 = BiomeBase.class.getDeclaredMethod("a", String.class);
            m2.setAccessible(true);

            Field ff2 = BiomeBase.class.getDeclaredField("ah");
            ff2.setAccessible(true);

            Method m3 = BiomeBase.class.getDeclaredMethod("a", int.class);
            m3.setAccessible(true);

            Method m4 = BiomeBase.class.getDeclaredMethod("a", float.class, float.class);
            m4.setAccessible(true);

            Field ff = BiomeBase.class.getDeclaredField("au");
            ff.setAccessible(true);

            ArrayList<BiomeBase.BiomeMeta> mobs = new ArrayList<>();

            mobs.add(new BiomeBase.BiomeMeta(EntitySheep.class, 15, 4, 4));
            mobs.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 15, 3, 5));
            mobs.add(new BiomeBase.BiomeMeta(EntityPig.class, 20, 4, 8));
            mobs.add(new BiomeBase.BiomeMeta(EntityChicken.class, 21, 5, 8));
            mobs.add(new BiomeBase.BiomeMeta(EntityCow.class, 20, 6, 8));
            mobs.add(new BiomeBase.BiomeMeta(EntityWolf.class, 6, 6, 15));

            ff.set(nb1, mobs);
            ff.set(nb2, mobs);

            m1.invoke(nb1, 353825, false);
            m2.invoke(nb1, "Forest");
            m3.invoke(nb1, 5159473);
            m4.invoke(nb1, 0.7F, 0.8F);

            m1.invoke(nb2, 353826, false);
            m2.invoke(nb2, "Forest");
            m3.invoke(nb2, 5159474);
            m4.invoke(nb2, 0.7F, 0.8F);

            Field f1 = BiomeBase.class.getDeclaredField("OCEAN");
            Field f2 = BiomeBase.class.getDeclaredField("DEEP_OCEAN");
            Field f3 = BiomeBase.class.getDeclaredField("ad");

            this.setFinalStatic(f1, nb1);
            this.setFinalStatic(f2, nb2);
            this.setFinalStatic(f3, nb1);

            a[0] = nb1;
            a[9] = null;
            a[10] = null;
            a[11] = null;
            a[12] = null;
            a[13] = null;
            a[14] = null;

            a[18] = null;

            a[21] = null;

            a[24] = nb2;
            a[25] = null;

            a[30] = null;
            a[31] = null;
            a[32] = null;
            a[33] = null;

            a[36] = null;
            a[37] = null;
            a[38] = null;
            a[39] = null;

            Field f4 = BiomeBase.class.getDeclaredField("biomes");
            this.setFinalStatic(f4, a);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setFinalStatic(Field field, Object obj) throws Exception
    {
        field.setAccessible(true);

        Field mf = Field.class.getDeclaredField("modifiers");
        mf.setAccessible(true);
        mf.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, obj);
    }
}
