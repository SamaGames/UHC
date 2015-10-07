package net.samagames.uhc.arena;

import com.google.gson.JsonElement;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import net.samagames.uhc.UHC;
import org.apache.commons.io.IOUtils;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WorldGenerator
{
    private final UHC plugin;
    private int lastShow = -1;
    private int numberChunk;

    public WorldGenerator(UHC plugin)
    {
        this.plugin = plugin;
    }

    public void begin(final World world)
    {
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
                        plugin.getLogger().info("Loading chunks (" + percentage + "%)");
                    }

                    z += 16;
                    if (z >= 600)

                    {
                        z = -600;
                        x += 16;
                    }

                    if (x >= 600)
                    {
                        this.cancel();
                        plugin.getArena().setStatus(Status.WAITING_FOR_PLAYERS);
                        return;
                    }

                    numberChunk++;
                    i++;
                }
            }
        }.runTaskTimer(this.plugin, 1L, 1L);
    }

    public boolean checkAndDownloadWorld()
    {
        SamaGamesAPI.get().getGameManager().getGameProperties().reload();

        File workDir = this.plugin.getDataFolder().getParentFile().getParentFile();
        File worldZip = new File(workDir, "worlds.zip");
        JsonElement worldStorage = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("worldStorage", null);

        if (worldStorage == null)
        {
            this.plugin.getLogger().severe("worldStorage not defined");
            return false;
        }

        URL worldStorageURL;
        String mapID = null;

        try
        {
            worldStorageURL = new URL(worldStorage.getAsString() + "get.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(worldStorageURL.openStream(), "UTF-8"));
            mapID = in.readLine();
            in.close();
            this.plugin.getLogger().info(mapID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        if (mapID != null)
        {
            if (worldZip.exists())
            {
                this.plugin.getLogger().warning("No map availaible but found worlds.zip in local, assuming to use it.");
                boolean result = this.extractWorlds(worldZip, workDir);

                try
                {
                    worldStorageURL = new URL(worldStorage + "clean.php?name=" + mapID);
                    URLConnection connection = worldStorageURL.openConnection();
                    connection.connect();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                return result;
            }

            return false;
        }
        else if (worldZip.exists())
        {
            this.plugin.getLogger().warning("worlds.zip already exist! Is that a Hydro managed server?");

            if (!worldZip.delete())
            {
                this.plugin.getLogger().severe("Cannot remove worlds.zip, this is a CRITICAL error!");
                return false;
            }
        }


        try
        {
            worldStorageURL = new URL(worldStorage + "download.php?name=" + mapID);
            ReadableByteChannel rbc = Channels.newChannel(worldStorageURL.openStream());
            FileOutputStream fos = new FileOutputStream(worldZip);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        try
        {
            worldStorageURL = new URL(worldStorage + "clean.php?name=" + mapID);
            URLConnection connection = worldStorageURL.openConnection();
            connection.connect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return this.extractWorlds(worldZip, workDir);
    }

    private boolean extractWorlds(File worldZip, File workDir)
    {
        try
        {
            ZipFile zipFile = new ZipFile(worldZip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(workDir, entry.getName());

                if (entry.isDirectory())
                {
                    if (!entryDestination.mkdirs())
                    {
                        this.plugin.getLogger().warning("Cannot create directory " + entryDestination);
                    }
                }
                else
                {
                    if (!entryDestination.getParentFile().mkdirs())
                    {
                        this.plugin.getLogger().warning("Cannot create directory for file " + entryDestination);
                    }

                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
            zipFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}