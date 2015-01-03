package fr.blueslime.uhc;

import fr.blueslime.uhc.arena.ArenaCommon;
import fr.blueslime.uhc.events.*;
import fr.blueslime.uhc.arena.SpawnBlock;
import fr.blueslime.uhc.commands.CommandAll;
import fr.blueslime.uhc.commands.CommandUHC;
import fr.blueslime.uhc.gui.Gui;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_8_R1.BiomeBase;
import net.minecraft.server.v1_8_R1.BiomeForest;
import net.minecraft.server.v1_8_R1.BiomeMeta;
import net.minecraft.server.v1_8_R1.EntityChicken;
import net.minecraft.server.v1_8_R1.EntityCow;
import net.minecraft.server.v1_8_R1.EntityPig;
import net.minecraft.server.v1_8_R1.EntityRabbit;
import net.minecraft.server.v1_8_R1.EntitySheep;
import net.minecraft.server.v1_8_R1.EntityWolf;
import net.samagames.gameapi.GameAPI;
import net.samagames.gameapi.json.Status;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class UHC extends JavaPlugin
{
    private static UHC plugin;
    private HashMap<UUID, Gui> playersGui;
    private ArenaCommon arena;
    private String bungeeName;
    private int comPort;
    private int startTimer;
    private boolean join;
    private boolean team;
    private SpawnBlock spawnBlock;

    @Override
    public void onEnable()
    {
        super.onEnable();
        plugin = this;
        
        this.playersGui = new HashMap<>();
        this.join = false;
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.patchBiomes();
        
        this.saveDefaultConfig();
        this.team = getConfig().getBoolean("team-mode", true);
                
        this.saveResource("lobby.schematic", false);
        this.getCommand("uhc").setExecutor(new CommandUHC());
        this.getCommand("all").setExecutor(new CommandAll());
        
        this.registerEvents();
                
        this.startTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {            
            @Override
            public void run()
            {
                if(Bukkit.getPluginManager().isPluginEnabled("MasterBundle"))
                {
                    finishInit();
                }
            } 
        }, 20L, 20L);
    }
    
    public void finishInit()
    {
        Bukkit.getScheduler().cancelTask(this.startTimer);
        
        this.spawnBlock = new SpawnBlock(this);
	this.spawnBlock.generate();
        
        this.comPort = getConfig().getInt("com-port");
        this.bungeeName = getConfig().getString("BungeeName");
        UUID uuid = UUID.fromString(getConfig().getString("ArenaID"));
        int maxPlayersInTeam = getConfig().getInt("max-players-in-team", 3);
        int teamNumber = getConfig().getInt("teams", 8);
        boolean animatedBorder = getConfig().getBoolean("animated-border", true);
        
        if(teamNumber > 12)
        {
            Bukkit.getLogger().severe("[UHC] Team number > 12 ! Setting 8 teams.");
            teamNumber = 8;
        }

        if(this.team)
            this.arena = new ArenaCommon(uuid, Bukkit.getWorld("world"), maxPlayersInTeam, teamNumber, animatedBorder);
        else
            this.arena = new ArenaCommon(uuid, Bukkit.getWorld("world"), 0, 0, animatedBorder);
        
        Bukkit.addRecipe(this.arena.getMelonRecipe());
    }
    
    public void finishGeneration()
    {
        boolean staffMode = getConfig().getBoolean("staff-mode", false);
        
        GameAPI.registerArena(this.arena);
        GameAPI.registerGame((staffMode ? "staffbeta" : (this.team ? "uhc_team" : "uhc_solo")), comPort, bungeeName);
        GameAPI.getManager().sendSync();
        
        this.join = true;
        this.getArena().getEasterEggManager().start();
    }

    @Override
    public void onDisable()
    {
        if(this.arena != null)
        {
            this.arena.setStatus(Status.Stopping);
        }
        
        GameAPI.getManager().sendSync();
        GameAPI.getManager().disable();
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
        Bukkit.getPluginManager().registerEvents(new UHCFinishJoinPlayerEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCFoodLevelChangeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCInventoryClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCInventoryCloseEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCJoinModEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerDropItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerInteractEntityEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerPickupItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerPreJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerQuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPlayerRespawnEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPotionSplashEvent(), this);
        //Bukkit.getPluginManager().registerEvents(new UHCRejoinPlayerEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCSignChangeEvent(), this);
    }
    
    public void patchBiomes()
    {
        BiomeBase[] a = BiomeBase.getBiomes();
        BiomeForest nb1 = new BiomeForest(0, 0);
        BiomeForest nb2 = new BiomeForest(24, 0);
 
        try
        {
            Method m1 = BiomeBase.class.getMethod("b", int.class);
            m1.setAccessible(true);
 
            Method m2 = BiomeBase.class.getMethod("a", String.class);
            m2.setAccessible(true);
 
            Method m3 = BiomeBase.class.getMethod("a", int.class);
            m3.setAccessible(true);
 
            Method m4 = BiomeBase.class.getMethod("a", float.class, float.class);
            m4.setAccessible(true);
            
            Field ff = BiomeBase.class.getDeclaredField("au");
            ff.setAccessible(true);

            List<BiomeMeta> mobs = new ArrayList<>();

            mobs.add(new BiomeMeta(EntitySheep.class, 12, 4, 4));
            mobs.add(new BiomeMeta(EntityRabbit.class, 10, 3, 3));
            mobs.add(new BiomeMeta(EntityPig.class, 10, 4, 4));
            mobs.add(new BiomeMeta(EntityChicken.class, 10, 4, 4));
            mobs.add(new BiomeMeta(EntityCow.class, 8, 4, 4));
            mobs.add(new BiomeMeta(EntityWolf.class, 5, 4, 4));

            ff.set(nb1, mobs);
            ff.set(nb2, mobs);
 
            m1.invoke(nb1, 353825);
            m2.invoke(nb1, "Oceane");
            m3.invoke(nb1, 5159473);
            m4.invoke(nb1, 0.7F, 0.8F);
 
            m1.invoke(nb2, 353825);
            m2.invoke(nb2, "Deep Oceane");
            m3.invoke(nb2, 5159473);
            m4.invoke(nb2, 0.7F, 0.8F);
 
            Field f1 = BiomeBase.class.getDeclaredField("OCEAN");
            Field f2 = BiomeBase.class.getDeclaredField("DEEP_OCEAN");
            this.setFinalStatic(f1, nb1);
            this.setFinalStatic(f2, nb2);
 
            a[0] = nb1;
            a[24] = nb2;
 
            Field f3 = BiomeBase.class.getDeclaredField("biomes");
            this.setFinalStatic(f3, a);
        }
        catch (Exception e) {}
    }
    
    public void setFinalStatic(Field field, Object obj) throws Exception
    {
        field.setAccessible(true);
 
        Field mf = Field.class.getDeclaredField("modifiers");
        mf.setAccessible(true);
        mf.setInt(field, field.getModifiers() & ~Modifier.FINAL);
 
        field.set(null, obj);
    }
    
    public void openGui(Player player, Gui gui)
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

    public int getComPort()
    {
        return this.comPort;
    }
    
    public String getBungeeName()
    {
        return this.bungeeName;
    }
    
    public Gui getPlayerGui(UUID uuid)
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

    public boolean canJoin()
    {
        return this.join;
    }
    
    public boolean isTeamMode()
    {
        return this.team;
    }
    
    public static UHC getPlugin()
    {
        return plugin;
    }
}