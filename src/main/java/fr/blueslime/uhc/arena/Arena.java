package fr.blueslime.uhc.arena;

import com.connorlinfoot.titleapi.TitleAPI;
import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.utils.CoinsUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import net.samagames.gameapi.GameAPI;
import net.samagames.gameapi.GameUtils;
import net.samagames.gameapi.json.Status;
import net.samagames.gameapi.themachine.CoherenceMachine;
import net.samagames.gameapi.types.GameArena;
import net.zyuiop.MasterBundle.StarsManager;
import net.zyuiop.coinsManager.CoinsManager;
import net.zyuiop.statsapi.StatsApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Arena implements GameArena
{    
    private final UUID arenaID;
    private final World world;
    private final CoherenceMachine machine;
    private final ArrayList<ArenaPlayer> arenaPlayers;
    private final ArrayList<UUID> arenaSpectators;
    private final ArrayList<ArenaTeam> arenaTeams;
    private final HashMap<UUID, Integer> playersCrash;
    private final HashMap<UUID, Integer> playersCrashTimer;
    private final int maxPlayers, minPlayers, maxPlayersInTeam;
    private final Scoreboard board;
    private final NumberFormat formatter;
    private final WorldBorder worldBorder;
    private String mapName;
    private BeginTimer timer;
    private Status status;
    private int episode;
    private int minutesLeft;
    private int secondsLeft;
    private boolean first30seconds;
    private boolean firstMinute;
    private int firstsTimer;
        
    public Arena(UUID arenaID, World world, int maxPlayersInTeam) 
    {
        this.arenaID = arenaID;
        this.arenaPlayers = new ArrayList<>();
        this.arenaSpectators = new ArrayList<>();
        this.playersCrash = new HashMap<>();
        this.playersCrashTimer = new HashMap<>();
        this.mapName = "Equipes de " + maxPlayersInTeam;
        this.machine = GameAPI.getCoherenceMachine("KTP");
        this.formatter = new DecimalFormat("00");
        this.timer = null;
        this.maxPlayers = maxPlayersInTeam * 8;
        this.minPlayers = (int) Math.floor(maxPlayers * 0.75);
        this.maxPlayersInTeam = maxPlayersInTeam;
        this.status = Status.Available;
        this.world = world;
        this.first30seconds = false;
        this.firstMinute = false;
        
        this.worldBorder = this.world.getWorldBorder();
        this.worldBorder.setCenter(0.0D, 0.0D);
        
        for(Entity entity : this.world.getEntities())
        {
            entity.remove();
        }
        
        this.world.setDifficulty(Difficulty.HARD);
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setTime(6000L);
        this.world.setStorm(false);
        this.world.setAnimalSpawnLimit(30);
        
        this.setupWorldBorder(50, 0, false, false);
        
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.board.registerNewObjective("uhc", "dummy");
        
        ArrayList<Location> spawns = new ArrayList<>();
        
        spawns.add(new Location(this.world, -500, 200, -500));
        spawns.add(new Location(this.world, -500, 200, 0));
        spawns.add(new Location(this.world, -500, 200, +500));
        spawns.add(new Location(this.world, 0, 200, -500));
        spawns.add(new Location(this.world, 0, 200, +500));
        spawns.add(new Location(this.world, +500, 200, -500));
        spawns.add(new Location(this.world, +500, 200, 0));
        spawns.add(new Location(this.world, +500, 200, +500));
        
        Collections.shuffle(spawns, new Random(System.nanoTime()));
        
        this.arenaTeams = new ArrayList<>();
        this.arenaTeams.add(new ArenaTeam(this, 0, this.maxPlayersInTeam, "Orange", ChatColor.GOLD, new ItemStack(Material.WOOL, 1, (short) 1), spawns.get(0).getBlockX(), spawns.get(0).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 1, this.maxPlayersInTeam, "Bleu", ChatColor.BLUE, new ItemStack(Material.WOOL, 1, (short) 11), spawns.get(1).getBlockX(), spawns.get(1).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 2, this.maxPlayersInTeam, "Rose", ChatColor.LIGHT_PURPLE, new ItemStack(Material.WOOL, 1, (short) 6), spawns.get(2).getBlockX(), spawns.get(2).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 3, this.maxPlayersInTeam, "Jaune", ChatColor.YELLOW, new ItemStack(Material.WOOL, 1, (short) 4), spawns.get(3).getBlockX(), spawns.get(3).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 4, this.maxPlayersInTeam, "Rouge", ChatColor.RED, new ItemStack(Material.WOOL, 1, (short) 14), spawns.get(4).getBlockX(), spawns.get(4).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 5, this.maxPlayersInTeam, "Vert", ChatColor.GREEN, new ItemStack(Material.WOOL, 1, (short) 13), spawns.get(5).getBlockX(), spawns.get(5).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 6, this.maxPlayersInTeam, "Violet", ChatColor.DARK_PURPLE, new ItemStack(Material.WOOL, 1, (short) 10), spawns.get(6).getBlockX(), spawns.get(6).getBlockZ()));
        this.arenaTeams.add(new ArenaTeam(this, 7, this.maxPlayersInTeam, "Gris", ChatColor.GRAY, new ItemStack(Material.WOOL, 1, (short) 7), spawns.get(7).getBlockX(), spawns.get(7).getBlockZ()));
        
        Objective healthObjective = this.board.registerNewObjective("Vie", "health");
        healthObjective.setDisplayName("Vie");
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
    
    public void joinPlayer(UUID playerID)
    {
        Player player = Bukkit.getPlayer(playerID);
        ArenaPlayer aPlayer = new ArenaPlayer(player);

        player.sendMessage(Messages.joinArena);
        player.teleport(new Location(this.world, 0.5D, 162, 0.5D));
        this.arenaPlayers.add(aPlayer);

        this.machine.getMessageManager().writePlayerJoinArenaMessage(player, this);

        refreshPlayers(true);
        setupPlayer(player);

        for(ArenaPlayer pPlayer : this.arenaPlayers)
        { 
            if(pPlayer.getPlayer().getPlayer() != null)
            {
                pPlayer.getPlayer().getPlayer().getPlayer().getPlayer().hidePlayer(player);
                player.hidePlayer(pPlayer.getPlayer().getPlayer().getPlayer().getPlayer());
            }
        }

        ArrayList<ArenaPlayer> removal = new ArrayList<>();

        for(ArenaPlayer pPlayer : this.arenaPlayers)
        {            
            if(pPlayer.getPlayer().getPlayer() == null)
            {
                removal.add(pPlayer);
                continue;
            }

            pPlayer.getPlayer().getPlayer().getPlayer().getPlayer().showPlayer(player);
            player.showPlayer(pPlayer.getPlayer().getPlayer().getPlayer().getPlayer());
        }

        for (ArenaPlayer pPlayer : removal)
        {
            this.arenaPlayers.remove(pPlayer);
        }

        player.getInventory().clear();
        player.getInventory().setItem(0, this.getSelectTeamItem());
        player.getInventory().setItem(8, this.getCoherenceMachine().getLeaveItem());
        player.setScoreboard(this.board);
        this.machine.getMessageManager().writeWelcomeInGameMessage(player);
        player.sendMessage(Messages.warningTeam);

        GameAPI.getManager().refreshArena(this);
    }

    public void refreshPlayers(boolean addPlayers)
    {        
        if(isGameStarted())
        {
            GameAPI.getManager().refreshArena(this);
            return;
        }
        
        if(timer != null && this.arenaPlayers.size() < this.minPlayers)
        {
            GameUtils.broadcastMessage(Messages.notEnougthPlayers);
            timer.setTimeout(0);
            timer.end();
            timer = null;
            
            status = Status.Available;
            
            GameAPI.getManager().refreshArena(this);
            return;
        }
        
        if (timer == null && this.arenaPlayers.size() >= this.minPlayers) 
        {
            timer = new BeginTimer(this);
            timer.start();
            
            status = Status.Starting;
            
            GameAPI.getManager().refreshArena(this);
        }
    }
    
    public void setupPlayer(Player player)
    {
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0D);
        player.setSaturation(20);
        player.getInventory().clear();
        player.setExp(0.0F);
        player.setLevel(0);
        
        for(PotionEffect pe : player.getActivePotionEffects())
            player.removePotionEffect(pe.getType());
    }

    public void startGame()
    {
        this.status = Status.InGame;
        
        if(this.timer != null)
            this.timer.end();
        
        this.timer = null;
        
        ArrayList<ArenaPlayer> remove = new ArrayList<>();
                        
        for(ArenaPlayer player : this.arenaPlayers)
        {
            Player p = player.getPlayer().getPlayer();
            
            if(p == null)
            {
                remove.add(player);
                continue;
            }
            
            if(!player.hasTeam())
            {
                for(ArenaTeam team : UHC.getPlugin().getArena().getTeamAlive())
                {
                    if(!team.isFull())
                    {
                        team.join(player);
                        break;
                    }
                }
            }
                        
            this.setupPlayer(p);
            
            p.setFireTicks(0);
            p.sendMessage(Messages.gameStart);
            p.teleport(player.getTeam().getSpawnLocation());
            p.setGameMode(GameMode.SURVIVAL);
            p.setExp(0.0F);
            p.setLevel(0);
            UHC.getPlugin().closeGui(p);
                        
            StatsApi.increaseStat(p, "uhc_team", "played_games", 1);
        }
        
        for(ArenaPlayer pPlayer : remove)
        {
            this.arenaPlayers.remove(pPlayer);
        }
        
        ArrayList<ArenaTeam> toRemove = new ArrayList<>();
        for(ArenaTeam team : this.arenaTeams)
        {
            if(team.isEmpty())
                toRemove.add(team);
        }
        
        for(ArenaTeam team : toRemove)
            this.arenaTeams.remove(team);

        GameAPI.getManager().refreshArena(this);
        
        this.episode = 1;
        this.minutesLeft = 20;
        this.secondsLeft = 0;
        
        this.setupWorldBorder(2000, 0, true, false);
        
        this.firstsTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {
            private int temp = 0;
            
            @Override
            public void run()
            {
                temp++;
                
                if(temp == 60)
                {
                    first30seconds = true;
                    GameUtils.broadcastMessage(Messages.damageActivated);
                }
                else if(temp == 600)
                {
                    firstMinute = true;
                    GameUtils.broadcastMessage(Messages.pvpActivated);
                    startGameCallback();
                }
            } 
        }, 20L, 20L);
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                updateScoreboard();
                secondsLeft--;
                
                if(secondsLeft == -1)
                {
                    minutesLeft--;
                    secondsLeft = 59;
                }
                
                if(minutesLeft == -1)
                {
                    minutesLeft = 20;
                    secondsLeft = 0;
                    GameUtils.broadcastMessage(ChatColor.AQUA + "-------- Fin jour " + episode + " --------");
                    episode++;
                    
                    if(episode == 3)
                    {
                        setupWorldBorder(400, 20*60*60, true, true);
                    }
                    else if(episode == 6)
                    {
                        GameUtils.broadcastMessage(Messages.reducted.replace("${COORDS}", "200x200"));
                    }
                }
            } 
        }, 20L, 20L);
                
        GameUtils.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[CONSEIL] Seulement vos alliÃ©s verront vos messages. Pour parler publiquement, prÃ©cÃ©dez votre message de '/all'");
    }
    
    public void startGameCallback()
    {
        Bukkit.getScheduler().cancelTask(this.firstsTimer);
    }
    
    public void setupWorldBorder(double distance, int time, boolean warning, boolean title)
    {        
        if(time == 0)
        {
            this.worldBorder.setSize(distance);
        }
        else
        {
            this.worldBorder.setSize(distance, time);  
        }
        
        this.worldBorder.setDamageBuffer(0.0D);
        this.worldBorder.setDamageAmount(0.0D);
        
        if(warning)
        {
            this.worldBorder.setWarningDistance(100);
            this.worldBorder.setWarningTime(30);
        }
        
        if(title)
        {
            for(Player player : Bukkit.getOnlinePlayers())
            {
                TitleAPI.sendTitle(player, 0, 100, 20, ChatColor.DARK_RED + "Attention !", ChatColor.RED + "La bordure de map se rapproche :)");
            }
            
            GameUtils.broadcastMessage(Messages.reducting.replace("${COORDS}", distance + "x" + distance));
        }
    }
    
    public void endGame()
    {
        this.status = Status.Stopping;
        GameAPI.getManager().refreshArena(this);
        
        for(ArenaPlayer player : this.arenaPlayers)
        {
            GameAPI.kickPlayer(player.getPlayer().getPlayer());
        }
        
        this.arenaPlayers.clear();
    }
    
    public void finish(ArenaTeam team)
    {         
        if(team != null)
        {
            this.machine.getMessageManager().writeSimpleEndMessage("L'Ã©quipe gagnante est l'Ã©quipe " + team.getChatColor() + team.getName());
            
            for(UUID player : team.getPlayers())
            {
                StatsApi.increaseStat(player, "uhc_team", "wins", 1);
                CoinsManager.creditJoueur(player, CoinsUtils.getWinCoins(this.episode), true, true, "Victoire");
                StarsManager.creditJoueur(player, 1, "Victoire");
            }
        }
        
        GameUtils.broadcastSound(Sound.WITHER_DEATH);
                
        Bukkit.getScheduler().runTaskLater(UHC.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                endGame();
            }
        }, 10*20L);
        
        Bukkit.getScheduler().runTaskLater(UHC.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                Bukkit.shutdown();
            }
        }, 12 * 20L);
    }
    
    public void updateScoreboard()
    {
        try
        {
            Objective obj = this.board.getObjective("uhc");

            obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "KTP" + ChatColor.RESET + " | " + ChatColor.AQUA + this.formatter.format(this.minutesLeft) + ":" + this.formatter.format(this.secondsLeft));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore(Bukkit.getOfflinePlayer("Jour:")).setScore(this.episode);
            obj.getScore(Bukkit.getOfflinePlayer("Joueurs:")).setScore(this.getActualPlayers());
            obj.getScore(Bukkit.getOfflinePlayer("Equipes:")).setScore(this.getTeamAlive().size());
        }
        catch(IllegalArgumentException | IllegalStateException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void lose(UUID player, boolean quitted)
    {
        GameUtils.broadcastSound(Sound.WITHER_SPAWN);
        StatsApi.increaseStat(player, "uhc_team", "deaths", 1);
        
        StatsApi.decreaseStat(player, "uhc_team", "max_episode", StatsApi.getPlayerStat(player, "uhc_team", "max_episode"));
        StatsApi.increaseStat(player, "uhc_team", "max_episode", this.episode);
        
        if(!quitted)
        {
            Player playerQuitted = Bukkit.getPlayer(player);
            
            if(playerQuitted.getKiller() != null)
            {
                Player killer = playerQuitted.getKiller();
                StatsApi.increaseStat(killer, "uhc_team", "kills", 1);
                CoinsManager.creditJoueur(killer.getUniqueId(), CoinsUtils.getKillCoins(this.episode), true, true, "Meurtre d'un joueur");
            }
        }
        
        this.getPlayer(player).getTeam().lose(this.getPlayer(player));
                
        if(this.getActualPlayers() == this.getPlayer(player).getTeam().getPlayers().size())
        {
            this.finish(this.getPlayer(player).getTeam());
        }
        
        this.arenaPlayers.remove(this.getPlayer(player));
        
        if(!quitted)
            this.arenaSpectators.add(player);
        else
            GameUtils.broadcastMessage(Messages.playerQuitted.replace("${PLAYER}", Bukkit.getOfflinePlayer(player).getName()));
    }
    
    public void loseTeam(ArenaTeam team)
    {
        GameUtils.broadcastMessage(Messages.teamEliminated.replace("${TEAM}", team.getChatColor() + team.getName() + ChatColor.RESET));
        this.arenaTeams.remove(team);
        
        if(this.arenaTeams.size() == 1)
            this.finish(this.arenaTeams.get(0));
    }
    
    public void loseRespawn(Player player)
    {
        loseHider(player);
        player.sendMessage(Messages.spectator);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/gamemode " + player.getName() + " 3");
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    }
    
    public void loseHider(Player player)
    {
        for(ArenaPlayer aPlayer : this.arenaPlayers)
        {
            aPlayer.getPlayer().getPlayer().hidePlayer(player);
        }
    }
    
    public void playerDisconnect(final UUID uuid)
    {
        this.playersCrashTimer.put(uuid, Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {
            int before = 0;
            boolean bool = false;
            
            @Override
            public void run()
            {
                if(!bool)
                {
                    if(playersCrash.containsKey(uuid))
                        before = playersCrash.get(uuid);
                    
                    bool = true;
                }
                
                if(before == 600)
                {
                    GameAPI.removeRejoinList(uuid);
                    lose(uuid, true);
                    playerReconnected(uuid);
                }
                
                before++;                
                playersCrash.put(uuid, before);
            }
        }, 20L, 20L));
    }
    
    public void playerReconnected(UUID uuid)
    {
        if(this.playersCrashTimer.containsKey(uuid))
            Bukkit.getScheduler().cancelTask(this.playersCrashTimer.get(uuid));
    }
    
    public void stumpPlayer(ArenaPlayer aPlayer)
    {        
        if(this.arenaPlayers.contains(aPlayer))
            this.arenaPlayers.remove(aPlayer);
        
        if (!this.isGameStarted())
        {
            this.refreshPlayers(false);
            return;
        }
        
        if (this.getActualPlayers() == 0)
        {
            this.finish(null);
        }
        else if (this.getArenaPlayers().isEmpty())
        {
            Bukkit.shutdown();
        }
        
        GameAPI.getManager().refreshArena(this);
    }
    
    public void createTeam(ArenaTeam team)
    {
        this.arenaTeams.add(team);
    }
    
    public void removeAdminTeam()
    {
        ArrayList<ArenaTeam> teams = new ArrayList<>(this.arenaTeams);
        
        for(ArenaTeam team : teams)
        {
            if(team.getIcon().getType() == Material.DIAMOND_BLOCK)
                this.arenaTeams.remove(team);
        }
    }
    
    public void addCoin(Player player, int count, String reason)
    {
        CoinsManager.creditJoueur(player.getUniqueId(), count, true, true, reason);
    }
    
    public void removePlayer(ArenaPlayer player)
    {
        if(this.arenaPlayers.contains(player))
        {
            if(this.getPlayer(player.getPlayerID()).hasTeam())
                this.getPlayer(player.getPlayerID()).getTeam().leave(player);
            
            this.arenaPlayers.remove(player);
        }
        
        GameUtils.broadcastMessage(Messages.playerQuitted.replace("${PLAYER}", player.getPlayer().getName()));
    }

    public void setMapName(String mapName)
    {
        this.mapName = mapName;
    }
    
    @Override
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    @Override
    public int countGamePlayers()
    {
        return this.getActualPlayers();
    }

    public String getMapName()
    {
        return this.mapName;
    }
    
    public World getWorld()
    {
        return this.world;
    }

    public ArenaPlayer getPlayer(UUID uuid)
    {
        for(ArenaPlayer aPlayer : this.arenaPlayers)
        {            
            if(aPlayer.getPlayerID().equals(uuid))
                return aPlayer;
        }
        
        return null;
    }
    
    public int getActualPlayers()
    {
        return this.arenaPlayers.size();
    }
    
    public ArrayList<ArenaPlayer> getArenaPlayers()
    {
        return this.arenaPlayers;
    }
    
    public ArrayList<ArenaTeam> getTeamAlive()
    {
        return this.arenaTeams;
    }
    
    public int getMinPlayers()
    {
        return this.minPlayers;
    }
    
    @Override
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    @Override
    public int getTotalMaxPlayers()
    {
        return this.maxPlayers;
    }

    @Override
    public Status getStatus()
    {
        return this.status;
    }

    public ShapelessRecipe getMelonRecipe()
    {
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
        recipe.addIngredient(1, Material.GOLD_BLOCK);
        recipe.addIngredient(1, Material.MELON);

        return recipe;
    }
    
    public ArenaTeam getAdminTeam()
    {
        for(ArenaTeam team : this.arenaTeams)
        {
            if(team.getIcon().getType() == Material.DIAMOND_BLOCK)
                return team;
        }
        
        return null;
    }
    
    public ArenaTeam getTeamByNumber(int number)
    {
        for(ArenaTeam team : this.arenaTeams)
        {
            if(team.getNumber() == number)
                return team;
        }
        
        return null;
    }
    
    @Override
    public int getVIPSlots()
    {
        return 0;
    }
    
    public int getMaxPlayersInTeam()
    {
        return this.maxPlayersInTeam;
    }

    @Override
    public UUID getUUID()
    {
        return this.arenaID;
    }
    
    public int getPlayerCrashTimerId(UUID uuid)
    {
        if(this.playersCrashTimer.containsKey(uuid))
            return this.playersCrashTimer.get(uuid);
        else
            return 0;
    }
    
    public ArrayList<UUID> getSpectators()
    {
        return this.arenaSpectators;
    }
    
    public ItemStack getSelectTeamItem()
    {
        ItemStack stack = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Choisir son Ã©quipe");
        stack.setItemMeta(meta);
        
        return stack;
    }
    
    public Scoreboard getScoreboard()
    {
        return this.board;
    }
    
    public CoherenceMachine getCoherenceMachine()
    {
        return this.machine;
    }

    @Override
    public boolean hasPlayer(UUID uuid)
    {
        for(ArenaPlayer player : this.arenaPlayers)
        {
            if(player.getPlayerID().equals(uuid))
                return true;
        }
        
        return false;
    }
    
    public boolean canJoin()
    {
        return ((status.equals(Status.Available) || status.equals(Status.Starting)) && this.arenaPlayers.size() < this.maxPlayers);
    }
    
    public boolean isGameStarted()
    {
        return (this.status == Status.InGame);
    }
    
    public boolean isSpectator(UUID uuid)
    {
        return this.arenaSpectators.contains(uuid);
    }

    @Override
    public boolean isFamous()
    {
        return false;
    }
    
    public boolean isFirst30Seconds()
    {
        return this.first30seconds;
    }
    
    public boolean isFirstMinute()
    {
        return this.firstMinute;
    }
    
    public boolean isAdminTeamExist()
    {
        for(ArenaTeam team : this.arenaTeams)
        {
            if(team.getIcon().getType() == Material.DIAMOND_BLOCK)
                return true;
        }
        
        return false;
    }
}
