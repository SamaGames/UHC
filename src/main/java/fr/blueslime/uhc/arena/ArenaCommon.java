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
import net.samagames.gameapi.themachine.messages.StaticMessages;
import net.samagames.gameapi.themachine.messages.templates.PlayerWinTemplate;
import net.samagames.gameapi.themachine.messages.templates.SpecifiedWinTemplate;
import net.samagames.gameapi.types.GameArena;
import net.samagames.utils.ObjectiveSign;
import net.samagames.utils.PlayerUtils;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ArenaCommon implements GameArena
{
    public static enum ArenaType { SOLO, TEAM };
    
    private final ArenaType arenaType;
    private final World world;
    private final CoherenceMachine machine;
    private final ArrayList<ArenaPlayer> arenaPlayers;
    private final ArrayList<UUID> arenaSpectators;
    private final ArrayList<ArenaTeam> arenaTeams;
    private final ArrayList<Location> spawns;
    private final HashMap<UUID, Integer> playersCrash;
    private final HashMap<UUID, Integer> playersCrashTimer;
    private final HashMap<UUID, Location> playersCrashLocation;
    private final HashMap<UUID, PlayerInventory> playersCrashInventory;
    private final int maxPlayers, minPlayers, maxPlayersInTeam;
    private final ObjectiveSign objective;
    private final Scoreboard board;
    private final NumberFormat formatter;
    private final WorldBorder worldBorder;
    private final WorldBorder netherBorder;
    private final EasterEggManager easterEggManager;
    private final boolean animatedBorder;
    private String mapName;
    private BukkitTask timer;
    private Status status;
    private int episode;
    private int minutesLeft;
    private int secondsLeft;
    private boolean first30seconds;
    private boolean firstMinute;
    private int firstsTimer;
    
    public ArenaCommon(World world, int maxPlayersInTeam, int teamNumber, boolean animatedBorder) 
    {
        this.arenaType = (maxPlayersInTeam == 0 ? ArenaType.SOLO : ArenaType.TEAM);
        this.arenaPlayers = new ArrayList<>();
        this.arenaSpectators = new ArrayList<>();
        this.playersCrash = new HashMap<>();
        this.playersCrashTimer = new HashMap<>();
        this.playersCrashLocation = new HashMap<>();
        this.playersCrashInventory = new HashMap<>();
        this.mapName = (maxPlayersInTeam == 0 ? "Solo" : "Equipes de " + maxPlayersInTeam);
        this.machine = GameAPI.getCoherenceMachine("UHC");
        this.formatter = new DecimalFormat("00");
        this.timer = null;
        this.maxPlayers = (maxPlayersInTeam == 0 ? 24 : maxPlayersInTeam * teamNumber);
        this.minPlayers = (maxPlayersInTeam == 0 ? 18 : (int) Math.floor(maxPlayers * 0.75));
        this.maxPlayersInTeam = maxPlayersInTeam;
        this.status = Status.Generating;
        this.easterEggManager = new EasterEggManager();
        this.animatedBorder = animatedBorder;
        this.world = world;
        this.first30seconds = false;
        this.firstMinute = false;
        
        this.worldBorder = this.world.getWorldBorder();
        this.worldBorder.setCenter(0.0D, 0.0D);
        this.netherBorder = Bukkit.getWorlds().get(1).getWorldBorder();
        this.netherBorder.setCenter(0.0D, 0.0D);
        this.setupWorldBorder(2000, 0, true, false);
        
        new WorldGenerator().begin(this, this.world);
        
        for(Entity entity : this.world.getEntities())
        {
            entity.remove();
        }
        
        this.world.setDifficulty(Difficulty.HARD);
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("randomTickSpeed", "30");
        this.world.setTime(6000L);
        this.world.setStorm(false);
                
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.board.registerNewObjective("uhc", "dummy");
        
        this.objective = new ObjectiveSign("uhcbar", ChatColor.GOLD + "" + ChatColor.BOLD + "UHC");
        
        this.spawns = new ArrayList<>();
        this.arenaTeams = new ArrayList<>();
        
        if(this.arenaType == ArenaType.SOLO)
        {
            this.spawns.add(new Location(this.world, 0, 200, 400));
            this.spawns.add(new Location(this.world, 0, 200, 800));
            this.spawns.add(new Location(this.world, 400, 200, 0));
            this.spawns.add(new Location(this.world, 800, 200, 400));
            this.spawns.add(new Location(this.world, 400, 200, 800));
            this.spawns.add(new Location(this.world, 800, 200, 800));
            this.spawns.add(new Location(this.world, 400, 200, 400));
            this.spawns.add(new Location(this.world, 0, 200, -400));
            this.spawns.add(new Location(this.world, 0, 200, -800));
            this.spawns.add(new Location(this.world, -400, 200, 0));
            this.spawns.add(new Location(this.world, -800, 200, 0));
            this.spawns.add(new Location(this.world, -800, 200, -400));
            this.spawns.add(new Location(this.world, -400, 200, -800));
            this.spawns.add(new Location(this.world, -800, 200, -800));
            this.spawns.add(new Location(this.world, -400, 200, -400));
            this.spawns.add(new Location(this.world, 800, 200, -400));
            this.spawns.add(new Location(this.world, -800, 200, 400));
            this.spawns.add(new Location(this.world, 400, 200, -800));
            this.spawns.add(new Location(this.world, -400, 200, 800));
            this.spawns.add(new Location(this.world, -800, 200, 800));
            this.spawns.add(new Location(this.world, 800, 200, -800));
            this.spawns.add(new Location(this.world, 400, 200, -400));
            this.spawns.add(new Location(this.world, -400, 200, 400));
        }
        else
        {
            this.spawns.add(new Location(this.world, 0, 200, 400));
            this.spawns.add(new Location(this.world, 0, 200, 800));
            this.spawns.add(new Location(this.world, 400, 200, 0));
            this.spawns.add(new Location(this.world, 800, 200, 0));
            this.spawns.add(new Location(this.world, 400, 200, 400));
            this.spawns.add(new Location(this.world, 0, 200, -400));
            this.spawns.add(new Location(this.world, 0, 200, -800));
            this.spawns.add(new Location(this.world, -400, 200, 0));
            this.spawns.add(new Location(this.world, -800, 200, 0));
            this.spawns.add(new Location(this.world, -400, 200, -400));
            this.spawns.add(new Location(this.world, 400, 200, -400));
            this.spawns.add(new Location(this.world, -400, 200, 400));
            
            ArrayList<ArenaTeam> temp = new ArrayList<>();
            
            temp.add(new ArenaTeam(this, 0, this.maxPlayersInTeam, "Blanc", ChatColor.WHITE, new ItemStack(Material.WOOL, 1, (short) 0), this.spawns.get(0).getBlockX(), this.spawns.get(0).getBlockZ()));
            temp.add(new ArenaTeam(this, 1, this.maxPlayersInTeam, "Orange", ChatColor.GOLD, new ItemStack(Material.WOOL, 1, (short) 1), this.spawns.get(1).getBlockX(), this.spawns.get(1).getBlockZ()));
            temp.add(new ArenaTeam(this, 2, this.maxPlayersInTeam, "Bleu clair", ChatColor.BLUE, new ItemStack(Material.WOOL, 1, (short) 3), this.spawns.get(2).getBlockX(), this.spawns.get(2).getBlockZ()));
            temp.add(new ArenaTeam(this, 3, this.maxPlayersInTeam, "Jaune", ChatColor.YELLOW, new ItemStack(Material.WOOL, 1, (short) 4), this.spawns.get(3).getBlockX(), this.spawns.get(3).getBlockZ()));
            temp.add(new ArenaTeam(this, 4, this.maxPlayersInTeam, "Rose", ChatColor.LIGHT_PURPLE, new ItemStack(Material.WOOL, 1, (short) 6), this.spawns.get(4).getBlockX(), this.spawns.get(4).getBlockZ()));
            temp.add(new ArenaTeam(this, 5, this.maxPlayersInTeam, "Gris", ChatColor.GRAY, new ItemStack(Material.WOOL, 1, (short) 7), this.spawns.get(5).getBlockX(), this.spawns.get(5).getBlockZ()));
            temp.add(new ArenaTeam(this, 6, this.maxPlayersInTeam, "Violet", ChatColor.DARK_PURPLE, new ItemStack(Material.WOOL, 1, (short) 10), this.spawns.get(6).getBlockX(), this.spawns.get(6).getBlockZ()));
            temp.add(new ArenaTeam(this, 7, this.maxPlayersInTeam, "Aqua", ChatColor.AQUA, new ItemStack(Material.WOOL, 1, (short) 9), this.spawns.get(7).getBlockX(), this.spawns.get(7).getBlockZ()));
            temp.add(new ArenaTeam(this, 8, this.maxPlayersInTeam, "Rouge", ChatColor.RED, new ItemStack(Material.WOOL, 1, (short) 14), this.spawns.get(10).getBlockX(), this.spawns.get(10).getBlockZ()));
            temp.add(new ArenaTeam(this, 9, this.maxPlayersInTeam, "Bleu foncé", ChatColor.DARK_BLUE, new ItemStack(Material.WOOL, 1, (short) 11), this.spawns.get(8).getBlockX(), this.spawns.get(8).getBlockZ()));
            temp.add(new ArenaTeam(this, 10, this.maxPlayersInTeam, "Vert foncé", ChatColor.DARK_GREEN, new ItemStack(Material.WOOL, 1, (short) 13), this.spawns.get(9).getBlockX(), this.spawns.get(9).getBlockZ()));
            temp.add(new ArenaTeam(this, 11, this.maxPlayersInTeam, "Noir", ChatColor.BLACK, new ItemStack(Material.WOOL, 1, (short) 15), this.spawns.get(11).getBlockX(), this.spawns.get(11).getBlockZ()));
            
            for(int i = 0; i < teamNumber; i++)
            {
                this.arenaTeams.add(temp.get(i));
            }
        }
        
        Collections.shuffle(this.spawns, new Random(System.nanoTime()));
        
        Objective healthObjective = this.board.registerNewObjective("Vie", "health");
        healthObjective.setDisplayName("Vie");
        healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        
        this.timer = Bukkit.getScheduler().runTaskTimer(UHC.getPlugin(), new BeginTimer(this), 20L, 20L);
    }
    
    public void joinPlayer(UUID playerID)
    {
        Player player = Bukkit.getPlayer(playerID);
        ArenaPlayer aPlayer = new ArenaPlayer(player);

        player.sendMessage(StaticMessages.JOINARENA.get(this.machine));
        player.teleport(new Location(this.world, 0.5D, 162, 0.5D));
        this.arenaPlayers.add(aPlayer);
        this.objective.addReceiver(player);
        this.machine.getMessageManager().writePlayerJoinArenaMessage(player, this);

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
        
        if(this.arenaType == ArenaType.TEAM)
            player.getInventory().setItem(0, this.getSelectTeamItem());
        
        player.getInventory().setItem(8, this.machine.getLeaveItem());
        player.setScoreboard(this.board);
        this.machine.getMessageManager().writeWelcomeInGameMessage(player);
        
        if(this.arenaType == ArenaType.TEAM)
            player.sendMessage(Messages.warningTeam);

        GameAPI.getManager().refreshArena(this);
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
        final ArenaCommon instance = this;
        
        if(this.timer != null)
            this.timer.cancel();
        
        this.timer = null;
        
        ArrayList<ArenaPlayer> remove = new ArrayList<>();
                        
        for(ArenaPlayer player : this.arenaPlayers)
        {
            Player p = player.getPlayer().getPlayer();
            
            if(p == null)
                remove.add(player);
        }
        
        for(ArenaPlayer pPlayer : remove)
        {
            this.arenaPlayers.remove(pPlayer);
        }
        
        for(int i = 0; i < this.arenaPlayers.size(); i++)
        {
            ArenaPlayer player = this.arenaPlayers.get(i);
            Player p = player.getPlayer().getPlayer();
            
            if(this.arenaType == ArenaType.TEAM)
            {
                if(!player.hasTeam())
                {
                    for(ArenaTeam team : UHC.getPlugin().getArena().getTeamAlive())
                    {
                        if(!team.isFull() && !team.isLocked())
                        {
                            team.join(player);
                            break;
                        }
                    }

                    if(!player.hasTeam())
                    {
                        player.getPlayer().sendMessage(ChatColor.RED + "Aucune team était apte à vous reçevoir, vous avez été réenvoyé dans le lobby.");
                        GameAPI.kickPlayer(player.getPlayer());
                    }
                }
            }
                        
            this.setupPlayer(p);
            
            p.setFireTicks(0);
            p.sendMessage(StaticMessages.GAMESTART.get(this.machine));
            
            System.out.println("Teleporting player: " + p.getName());
            
            if(this.arenaType == ArenaType.TEAM)
                p.teleport(player.getTeam().getSpawnLocation());
            else
                p.teleport(this.spawns.get(i));
            
            p.setGameMode(GameMode.SURVIVAL);
            p.setExp(0.0F);
            p.setLevel(0);
            
            for(Player p2 : Bukkit.getOnlinePlayers())
            {
                if(!p2.getName().equals(p.getName()))
                    p.showPlayer(p2);
            }
            
            UHC.getPlugin().closeGui(p);
            this.increaseStat(p.getUniqueId(), "played_games", 1);
        }
        
        if(this.arenaType == ArenaType.TEAM)
        {
            ArrayList<ArenaTeam> toRemove = new ArrayList<>();
            for(ArenaTeam team : this.arenaTeams)
            {
                if(team.isEmpty())
                    toRemove.add(team);
            }

            for(ArenaTeam team : toRemove)
                this.arenaTeams.remove(team);
        }

        GameAPI.getManager().refreshArena(this);
                
        this.episode = 1;
        this.minutesLeft = 20;
        this.secondsLeft = 0;
                
        UHC.getPlugin().getSpawnBlock().remove();
        
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
                    
                    if(episode == 8 && minutesLeft == 10)
                    {
                        if(animatedBorder)
                            setupWorldBorder(50, 60*2, true, true);
                    }
                    else if(episode == 8 && minutesLeft == 8)
                    {
                        GameUtils.broadcastMessage(Messages.reducted.replace("${COORDS}", "-25 25"));
                    }
                    else if(episode == 8 && minutesLeft == 1)
                    {
                        EndTimer endTimer = new EndTimer(instance);
                        endTimer.start();
                    }
                }
                
                if(minutesLeft == -1)
                {
                    minutesLeft = 20;
                    secondsLeft = 0;
                    GameUtils.broadcastMessage(ChatColor.AQUA + "-------- Fin jour " + episode + " --------");
                    easterEggManager.nextEpisode();
                    episode++;
                    
                    if(episode == 2)
                    {
                        firstMinute = true;
                        GameUtils.broadcastMessage(Messages.pvpActivated);
                    }
                    else if(episode == 4)
                    {
                        if(animatedBorder)
                            setupWorldBorder(200, 60*60, true, true);
                    }
                    else if(episode == 7)
                    {
                        GameUtils.broadcastMessage(Messages.reducted.replace("${COORDS}", "-100 100"));
                    }
                    else if(episode == 8)
                    {
                        GameUtils.broadcastMessage(Messages.endOfGameAt);
                    }
                }
            } 
        }, 20L, 20L);
                
        if(this.arenaType == ArenaType.TEAM)
            GameUtils.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[CONSEIL] Seulement vos alliés verront vos messages. Pour parler publiquement, précédez votre message de '/all'");
    }
    
    public void startGameCallback()
    {
        Bukkit.getScheduler().cancelTask(this.firstsTimer);
    }
    
    public void setupWorldBorder(double distance, int time, boolean warning, boolean title)
    {        
        this.worldBorder.setDamageBuffer(0.0D);
        this.worldBorder.setDamageAmount(0.0D);
        this.netherBorder.setDamageBuffer(0.0D);
        this.netherBorder.setDamageAmount(0.0D);
        
        if(warning)
        {
            this.worldBorder.setWarningDistance(100);
            this.worldBorder.setWarningTime(30);
            this.netherBorder.setWarningDistance(100);
            this.netherBorder.setWarningTime(30);
        }
        
        if(title)
        {
            GameUtils.broadcastSound(Sound.BLAZE_DEATH);
            
            for(Player player : Bukkit.getOnlinePlayers())
            {
                TitleAPI.sendTitle(player, 0, 100, 20, ChatColor.DARK_RED + "Attention !", ChatColor.RED + "La bordure de map se rapproche :)");
            }
            
            GameUtils.broadcastMessage(Messages.reducting.replace("${COORDS}", "-" + distance + " " + distance));
        }
        
        if(time == 0)
        {
            try
            {
                this.worldBorder.setSize(distance);
                this.netherBorder.setSize(distance);
            }
            catch(Exception ex)
            {
                GameUtils.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Une erreur s'est produite durant la modification de la bordure de map ! Essai manuel...");
                ex.printStackTrace();
                
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set " + distance);
            }
        }
        else
        {
            try
            {
                this.worldBorder.setSize(distance, time);
                this.netherBorder.setSize(distance, time);
            }
            catch(Exception ex)
            {
                GameUtils.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Une erreur s'est produite durant la modification de la bordure de map ! Essai manuel...");
                ex.printStackTrace();
                
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set " + distance + " " + time);
            }
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
        this.arenaSpectators.clear();
        this.arenaTeams.clear();
        this.playersCrash.clear();
        this.playersCrashTimer.clear();
        this.spawns.clear();
    }
    
    public void finishTeam(ArenaTeam team)
    {         
        if(team != null)
        {
            StringBuilder players = new StringBuilder();
                                    
            for(UUID player : team.getPlayers())
            {
                increaseStat(player, "wins", 1);
                CoinsManager.creditJoueur(player, CoinsUtils.getWinCoins(this.episode), true, true, "Victoire");
                StarsManager.creditJoueur(player, this.episode * 2, "Victoire");
                
                players.append(PlayerUtils.getColoredFormattedPlayerName(Bukkit.getPlayer(player))).append(ChatColor.GRAY).append(", ");
            }
            
            String commentary = players.substring(0, players.length() - 2);
            new SpecifiedWinTemplate().execute("Equipe " + team.getChatColor() + team.getName(), commentary);
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
    
    public void finishSolo(ArenaPlayer player)
    {         
        if(player != null)
        {
            new PlayerWinTemplate().execute(player.getPlayer());
            increaseStat(player.getPlayerID(), "wins", 1);
            CoinsManager.creditJoueur(player.getPlayerID(), CoinsUtils.getWinCoins(this.episode), true, true, "Victoire");
            StarsManager.creditJoueur(player.getPlayerID(), this.episode * 2, "Victoire");
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
        int team = -4;
        
        this.objective.setLine(-1, ChatColor.GRAY + "Jour: " + ChatColor.WHITE + this.episode);
        this.objective.setLine(-2, ChatColor.WHITE + "");
        this.objective.setLine(-3, ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + this.getActualPlayers());
        
        if(this.arenaType == ArenaType.TEAM)
        {
            this.objective.setLine(-4, ChatColor.GRAY + "Equipes: " + ChatColor.WHITE + this.getTeamAlive().size());
            team = -5;
        }
        
        this.objective.setLine(team, ChatColor.GRAY + "");
        this.objective.setLine(team - 1, ChatColor.GRAY + "Bordure:");
        this.objective.setLine(team - 2, ChatColor.WHITE + "-" + String.valueOf(((int) this.worldBorder.getSize()) / 2) + " +" + String.valueOf(((int) this.worldBorder.getSize()) / 2));
        this.objective.setLine(team - 3, ChatColor.RED + "");
        this.objective.setLine(team - 4, ChatColor.WHITE + this.formatter.format(this.minutesLeft) + ":" + this.formatter.format(this.secondsLeft));
    
        this.objective.updateLines();
    }
    
    public void lose(UUID player, boolean quitted)
    {
        GameUtils.broadcastSound(Sound.WITHER_SPAWN);
        increaseStat(player, "deaths", 1);
        
        if(!quitted)
        {
            if(Bukkit.getPlayer(player) != null)
            {
                Player playerQuitted = Bukkit.getPlayer(player);

                if(playerQuitted.getKiller() != null)
                {
                    Player killer = playerQuitted.getKiller();

                    if(!killer.getUniqueId().equals(playerQuitted.getUniqueId()))
                    {
                        increaseStat(killer.getUniqueId(), "kills", 1);
                        CoinsManager.creditJoueur(killer.getUniqueId(), CoinsUtils.getKillCoins(this.episode), true, true, "Meurtre d'un joueur");
                    }
                }
            }
        }
        else
        {
            if(Bukkit.getPlayer(player) != null)
            {
                Player playerQuitted = Bukkit.getPlayer(player);
                
                for(ItemStack stack : playerQuitted.getInventory().getContents())
                {
                    if(stack != null && stack.getType() != Material.AIR)
                        playerQuitted.getWorld().dropItem(playerQuitted.getLocation(), stack);
                }
                
                for(ItemStack stack : playerQuitted.getInventory().getArmorContents())
                {
                    if(stack != null && stack.getType() != Material.AIR)
                        playerQuitted.getWorld().dropItem(playerQuitted.getLocation(), stack);
                }
            }
        }
        
        if(this.arenaType == ArenaType.TEAM)
        {
            this.getPlayer(player).getTeam().lose(this.getPlayer(player));
                
            if(this.getActualPlayers() == this.getPlayer(player).getTeam().getPlayers().size())
            {
                this.finishTeam(this.getPlayer(player).getTeam());
            }
            
            this.arenaPlayers.remove(this.getPlayer(player));
        }
        else
        {
            this.arenaPlayers.remove(this.getPlayer(player));
            
            if(this.getActualPlayers() == 1)
            {
                this.finishSolo(this.arenaPlayers.get(0));
            }
        }
        
        if(!quitted)
            this.arenaSpectators.add(player);
    }
    
    public void loseTeam(ArenaTeam team)
    {
        GameUtils.broadcastMessage(Messages.teamEliminated.replace("${TEAM}", team.getChatColor() + team.getName() + ChatColor.RESET));
        this.arenaTeams.remove(team);
        
        if(this.arenaTeams.size() == 1)
            this.finishTeam(this.arenaTeams.get(0));
    }
    
    public void loseRespawn(Player player)
    {
        loseHider(player);
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.setGameMode(GameMode.SPECTATOR);
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
        GameAPI.addRejoinList(uuid, 180);
        GameUtils.broadcastMessage(Messages.disconnected.replace("${PLAYER}", Bukkit.getOfflinePlayer(uuid).getName()));
        this.objective.removeReceiver(Bukkit.getOfflinePlayer(uuid));
        
        if(this.playersCrashLocation.containsKey(uuid))
            this.playersCrashLocation.remove(uuid);
        
        if(this.playersCrashInventory.containsKey(uuid))
            this.playersCrashInventory.remove(uuid);
        
        if(Bukkit.getPlayer(uuid) != null)
        {
            this.playersCrashLocation.put(uuid, Bukkit.getPlayer(uuid).getLocation());
            this.playersCrashInventory.put(uuid, Bukkit.getPlayer(uuid).getInventory());
        }
        
        this.playersCrashTimer.put(uuid, Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {
            int before = 0;
            int now = 0;
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
                
                if(before == 300)
                {
                    GameAPI.removeRejoinList(uuid);
                    lose(uuid, true);
                    playerReconnected(uuid, true);
                }
                
                if(now == 180)
                {
                    GameAPI.removeRejoinList(uuid);
                    lose(uuid, true);
                    playerReconnected(uuid, true);
                }
                
                before++;
                now++;
                playersCrash.put(uuid, before);
            }
        }, 20L, 20L));
    }
    
    public void playerReconnected(UUID uuid, boolean outoftime)
    {
        if(this.playersCrashTimer.containsKey(uuid))
            Bukkit.getScheduler().cancelTask(this.playersCrashTimer.get(uuid));
        
        if(outoftime)
        {
            GameUtils.broadcastMessage(Messages.timeOut.replace("${PLAYER}", Bukkit.getOfflinePlayer(uuid).getName()));
            
            if(this.playersCrashLocation.containsKey(uuid) && this.playersCrashInventory.containsKey(uuid))
            {
                for(ItemStack stack : this.playersCrashInventory.get(uuid).getContents())
                {
                    if(stack != null && stack.getType() != Material.AIR)
                        this.world.dropItemNaturally(this.playersCrashLocation.get(uuid), stack);
                }
                
                for(ItemStack stack : this.playersCrashInventory.get(uuid).getArmorContents())
                {
                    if(stack != null && stack.getType() != Material.AIR)
                        this.world.dropItemNaturally(this.playersCrashLocation.get(uuid), stack);
                }
            }
            
            return;
        }
        
        GameUtils.broadcastMessage(Messages.reconnected.replace("${PLAYER}", Bukkit.getPlayer(uuid).getName()));
        Bukkit.getPlayer(uuid).setScoreboard(this.board);
        Bukkit.getPlayer(uuid).setGameMode(GameMode.SURVIVAL);
        this.objective.addReceiver(Bukkit.getPlayer(uuid));
    }
    
    public void stumpPlayer(ArenaPlayer aPlayer)
    {        
        if(this.arenaPlayers.contains(aPlayer))
            this.arenaPlayers.remove(aPlayer);
        
        if (!this.isGameStarted())
        {
            GameAPI.getManager().refreshArena(this);
            return;
        }
        
        if (this.getActualPlayers() == 0)
        {
            this.finishSolo(null);
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
            if(team.getIcon().getType() == Material.COOKIE)
                this.arenaTeams.remove(team);
        }
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
    
    public void increaseStat(UUID uuid, String statName, int count)
    {
        StatsApi.increaseStat(uuid, "uhc_" + (this.arenaType == ArenaType.TEAM ? "team" : "solo"), statName, count);
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
    
    public int getStat(UUID uuid, String statName)
    {
        return StatsApi.getPlayerStat(uuid, "uhc_" + (this.arenaType == ArenaType.TEAM ? "team" : "solo"), statName);
    }
    
    public ArenaType getArenaType()
    {
        return this.arenaType;
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
        return this.maxPlayers - (this.arenaType == ArenaType.TEAM ? this.maxPlayersInTeam : 4);
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
            if(team.getIcon().getType() == Material.COOKIE)
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
        return this.maxPlayersInTeam;
    }
    
    public int getMaxPlayersInTeam()
    {
        return this.maxPlayersInTeam;
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
        meta.setDisplayName(ChatColor.AQUA + "Choisir son équipe");
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
    
    public EasterEggManager getEasterEggManager()
    {
        return this.easterEggManager;
    }
    
    public ObjectiveSign getObjectiveSign()
    {
        return this.objective;
    }
    
    public WorldBorder getWorldBorder()
    {
        return this.worldBorder;
    }
    
    @Override
    public int countGamePlayers()
    {
        return this.getActualPlayers();
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
            if(team.getIcon().getType() == Material.COOKIE)
                return true;
        }
        
        return false;
    }
}
