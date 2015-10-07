package net.samagames.uhc;

import com.google.gson.JsonPrimitive;
import net.minecraft.server.v1_8_R3.*;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;
import net.samagames.api.games.Status;
import net.samagames.api.gui.AbstractGui;
import net.samagames.uhc.arena.ArenaCommon;
import net.samagames.uhc.arena.SpawnBlock;
import net.samagames.uhc.arena.WorldGenerator;
import net.samagames.uhc.commands.CommandAll;
import net.samagames.uhc.commands.CommandUHC;
import net.samagames.uhc.events.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class UHC extends JavaPlugin
{
    private static UHC plugin;
    private HashMap<UUID, AbstractGui> playersGui;
    private ArenaCommon arena;
    private SpawnBlock spawnBlock;
    private BukkitTask task;
    private WorldGenerator generator;

    @Override
    public void onEnable()
    {
        plugin = this;
        
        this.playersGui = new HashMap<>();

        this.generator = new WorldGenerator(this);
        this.generator.checkAndDownloadWorld();
        
        this.patchBiomes();

        this.saveResource("lobby.schematic", false);
        this.getCommand("uhc").setExecutor(new CommandUHC());
        this.getCommand("all").setExecutor(new CommandAll());

        this.registerEvents();

        this.task = Bukkit.getScheduler().runTaskTimer(this, this::finishInit, 20L, 20L);
    }
    
    public void finishInit()
    {
        this.task.cancel();

        IGameProperties properties = SamaGamesAPI.get().getGameManager().getGameProperties();

        boolean animatedBorders = properties.getOption("animated-borders", new JsonPrimitive(true)).getAsBoolean();
        boolean teamMode = properties.getOption("team-mode", new JsonPrimitive(false)).getAsBoolean();
        int maxPlayersInTeam = properties.getOption("max-players-in-team", new JsonPrimitive(2)).getAsInt();
        int teamNumber = properties.getOption("teams", new JsonPrimitive(12)).getAsInt();

        if(teamNumber > 12)
        {
            this.getServer().getLogger().severe("[UHC] Team number > 12 ! Setting 8 teams.");
            teamNumber = 8;
        }

        this.generator.begin(this.getServer().getWorld("world"));

        if(teamMode)
            this.arena = new ArenaCommon(this.getServer().getWorld("world"), maxPlayersInTeam, teamNumber, animatedBorders);
        else
            this.arena = new ArenaCommon(this.getServer().getWorld("world"), 0, 0, animatedBorders);

        SamaGamesAPI.get().getGameManager().registerGame(this.arena);
        SamaGamesAPI.get().getGameManager().setMaxReconnectTime(5);

        this.getServer().addRecipe(this.arena.getMelonRecipe());

        this.spawnBlock = new SpawnBlock(this);
        this.spawnBlock.generate();
    }

    @Override
    public void onDisable()
    {
        if(this.arena != null)
            this.arena.setStatus(Status.REBOOTING);
    }
    
    public void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new UHCAsyncPlayerChatEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCBlockBreakEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCBlockPlaceEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCBrewEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCCraftItemEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCEntityDamageByEntityEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCEntityDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCEntityDeathEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCEntityRegainHealthEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCEntityTargetEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCFoodLevelChangeEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCInventoryClickEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCInventoryCloseEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerDeathEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerDropItemEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerInteractEntityEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerInteractEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerMoveEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerPickupItemEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPotionSplashEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCSignChangeEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCBlockFromToEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerBucketEmptyEvent(), this);
        this.getServer().getPluginManager().registerEvents(new UHCPlayerPortalEvent(), this);
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
    
    public void openGui(Player player, AbstractGui gui)
    {
        if(this.playersGui.containsKey(player.getUniqueId()))
        {
            player.closeInventory();
            this.playersGui.remove(player.getUniqueId());
        }
        
        this.playersGui.put(player.getUniqueId(), gui);
        gui.display(player);
    }
    
    public void closeGui(Player player)
    {
        if(this.playersGui.containsKey(player.getUniqueId()))
        {
            player.closeInventory();
            this.playersGui.remove(player.getUniqueId());
        }
    }
    
    public void removePlayerFromList(UUID uuid)
    {
        if(this.playersGui.containsKey(uuid))
        {
            this.playersGui.remove(uuid);
        }
    }

    public ArenaCommon getArena()
    {
        return this.arena;
    }
    
    public AbstractGui getPlayerGui(UUID uuid)
    {
        if(this.playersGui.containsKey(uuid))
        {
            return this.playersGui.get(uuid);
        }
        else
        {
            return null;
        }
    }
    
    public SpawnBlock getSpawnBlock()
    {
        return this.spawnBlock;
    }
    
    public static UHC getPlugin()
    {
        return plugin;
    }
}