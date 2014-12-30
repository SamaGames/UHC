package fr.blueslime.uhc;

import fr.blueslime.uhc.arena.ArenaGameSolo;
import fr.blueslime.uhc.events.common.*;
import fr.blueslime.uhc.arena.ArenaGameTeam;
import fr.blueslime.uhc.arena.SpawnBlock;
import fr.blueslime.uhc.commands.CommandAll;
import fr.blueslime.uhc.commands.CommandUHC;
import fr.blueslime.uhc.gui.Gui;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.v1_8_R1.BiomeBase;
import net.minecraft.server.v1_8_R1.BiomeForest;
import net.samagames.gameapi.GameAPI;
import net.samagames.gameapi.json.Status;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class UHC extends JavaPlugin
{
    private static UHC plugin;
    private HashMap<UUID, Gui> playersGui;
    private ArenaGameTeam arenaTeam = null;
    private ArenaGameSolo arenaSolo = null;
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
        
        this.join = false;
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.patchBiomes();
        
        this.saveDefaultConfig();
        this.team = getConfig().getBoolean("team-mode", true);
                
        this.saveResource("lobby.schematic", false);
        this.getCommand("uhc").setExecutor(new CommandUHC());
        
        if(this.team)
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

        if(this.team)
        {
            this.arenaTeam = new ArenaGameTeam(uuid, Bukkit.getWorld("world"), maxPlayersInTeam);
            GameAPI.registerArena(this.arenaTeam);
            GameAPI.registerGame("staffbeta", comPort, bungeeName);
            Bukkit.addRecipe(this.arenaTeam.getMelonRecipe());
        }
        else
        {
            this.arenaSolo = new ArenaGameSolo(uuid, Bukkit.getWorld("world"));
            GameAPI.registerArena(this.arenaSolo);
            GameAPI.registerGame("staffbeta", comPort, bungeeName);
            Bukkit.addRecipe(this.arenaSolo.getMelonRecipe());
        }
        
        this.playersGui = new HashMap<>();
        GameAPI.getManager().sendArenas();
        this.join = true;
    }

    @Override
    public void onDisable()
    {
        if(this.arenaTeam != null)
        {
            this.arenaTeam.setStatus(Status.Stopping);
        }
        
        if(this.arenaSolo != null)
        {
            this.arenaSolo.setStatus(Status.Stopping);
        }
        
        GameAPI.getManager().sendSync();
        GameAPI.getManager().disable();
    }
    
    public void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new UHCInventoryClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCInventoryCloseEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCCraftItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCBrewEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCEntityRegainHealthEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPotionSplashEvent(), this);
        
        if(this.team)
        {
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCAsyncPlayerChatEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCBlockBreakEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCBlockPlaceEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCEntityDamageByEntityEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCEntityDamageEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCEntityTargetEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCFinishJoinPlayerEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCFoodLevelChangeEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCJoinModEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerDeathEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerDropItemEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerInteractEntityEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerInteractEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerPickupItemEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerPreJoinEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerQuitEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCPlayerRespawnEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCRejoinPlayerEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.team.UHCSignChangeEvent(), this);
        }
        else
        {
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCBlockBreakEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCBlockPlaceEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCEntityDamageByEntityEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCEntityDamageEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCEntityTargetEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCFinishJoinPlayerEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCFoodLevelChangeEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCJoinModEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerDeathEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerDropItemEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerInteractEntityEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerInteractEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerPickupItemEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerPreJoinEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerQuitEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCPlayerRespawnEvent(), this);
            Bukkit.getPluginManager().registerEvents(new fr.blueslime.uhc.events.solo.UHCRejoinPlayerEvent(), this);
        }
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

    public ArenaGameTeam getArenaTeam()
    {
        return this.arenaTeam;
    }
    
    public ArenaGameSolo getArenaSolo()
    {
        return this.arenaSolo;
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