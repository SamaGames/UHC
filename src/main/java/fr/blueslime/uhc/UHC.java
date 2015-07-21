package fr.blueslime.uhc;

import com.google.gson.JsonPrimitive;
import fr.blueslime.uhc.arena.ArenaCommon;
import fr.blueslime.uhc.arena.SpawnBlock;
import fr.blueslime.uhc.commands.CommandAll;
import fr.blueslime.uhc.commands.CommandUHC;
import fr.blueslime.uhc.events.*;
import net.minecraft.server.v1_8_R3.*;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;
import net.samagames.api.games.Status;
import net.samagames.tools.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Override
    public void onEnable()
    {
        super.onEnable();
        plugin = this;
        
        this.playersGui = new HashMap<>();
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.patchBiomes();

        this.saveResource("lobby.schematic", false);
        this.getCommand("uhc").setExecutor(new CommandUHC());
        this.getCommand("all").setExecutor(new CommandAll());

        this.registerEvents();
    }
    
    public void finishInit()
    {
        IGameProperties properties = SamaGamesAPI.get().getGameManager().getGameProperties();

        boolean animatedBorders = properties.getOption("animated-borders", new JsonPrimitive(true)).getAsBoolean();
        boolean teamMode = properties.getOption("team-mode", new JsonPrimitive(false)).getAsBoolean();
        int maxPlayersInTeam = properties.getOption("max-players-in-team", new JsonPrimitive(2)).getAsInt();
        int teamNumber = properties.getOption("teams", new JsonPrimitive(12)).getAsInt();

        if(teamNumber > 12)
        {
            Bukkit.getLogger().severe("[UHC] Team number > 12 ! Setting 8 teams.");
            teamNumber = 8;
        }

        if(teamMode)
            this.arena = new ArenaCommon(Bukkit.getWorld("world"), maxPlayersInTeam, teamNumber, animatedBorders);
        else
            this.arena = new ArenaCommon(Bukkit.getWorld("world"), 0, 0, animatedBorders);

        this.arena.setStatus(Status.STARTING);
        SamaGamesAPI.get().getGameManager().registerGame(this.arena);
        SamaGamesAPI.get().getGameManager().setMaxReconnectTime(5);

        this.spawnBlock = new SpawnBlock(this);
        this.spawnBlock.generate();

        Bukkit.addRecipe(this.arena.getMelonRecipe());
    }
    
    public void finishGeneration()
    {
        this.arena.setStatus(Status.WAITING_FOR_PLAYERS);
        this.arena.getEasterEggManager().start();
    }

    @Override
    public void onDisable()
    {
        if(this.arena != null)
            this.arena.setStatus(Status.REBOOTING);
    }
    
    public void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new UHCAsyncPlayerChatEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCBlockBreakEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCBlockPlaceEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCBrewEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCCraftItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityDamageByEntityEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityDamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityRegainHealthEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityTargetEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCFoodLevelChangeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCInventoryClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCInventoryCloseEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerDropItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerInteractEntityEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerPickupItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerRespawnEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPotionSplashEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCSignChangeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCBlockFromToEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerBucketEmptyEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerPortalEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCWorldInitEvent(), this);
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