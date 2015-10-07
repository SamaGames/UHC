package net.samagames.uhc.arena;

import net.samagames.api.games.Game;
import net.samagames.api.games.Status;
import net.samagames.tools.GameUtils;
import net.samagames.tools.PlayerUtils;
import net.samagames.tools.Titles;
import net.samagames.tools.chat.ActionBarAPI;
import net.samagames.tools.chat.ChatUtils;
import net.samagames.tools.scoreboards.ObjectiveSign;
import net.samagames.uhc.Messages;
import net.samagames.uhc.UHC;
import net.samagames.uhc.utils.CoinsUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class ArenaCommon extends Game<ArenaPlayer>
{
    public enum ArenaType { SOLO, TEAM };
    
    private final ArenaType arenaType;
    private final World world;
    private final HashMap<UUID, Location> disconnectLocation;
    private final HashMap<UUID, PlayerInventory> disconnectInventory;
    private final ArrayList<ArenaTeam> arenaTeams;
    private final ArrayList<Location> spawns;
    private final ObjectiveSign objective;
    private final Scoreboard board;
    private final NumberFormat formatter;
    private final WorldBorder worldBorder;
    private final WorldBorder netherBorder;
    private final EasterEggManager easterEggManager;
    private final int maxPlayersInTeam;
    private final boolean animatedBorders;
    private int episode;
    private int minutesLeft;
    private int secondsLeft;
    private int firstsTimer;
    private boolean firstMinute;
    private boolean firstTenMinutes;

    public ArenaCommon(World world, int maxPlayersInTeam, int teamNumber, boolean animatedBorders)
    {
        super("uhc", "UHC", "La survie en difficulté Hard !", ArenaPlayer.class);

        this.arenaType = (maxPlayersInTeam == 0 ? ArenaType.SOLO : ArenaType.TEAM);
        this.disconnectLocation = new HashMap<>();
        this.disconnectInventory = new HashMap<>();
        this.formatter = new DecimalFormat("00");
        this.maxPlayersInTeam = maxPlayersInTeam;
        this.easterEggManager = new EasterEggManager();
        this.world = world;
        this.firstMinute = false;
        this.firstTenMinutes = false;
        this.animatedBorders = animatedBorders;
        
        this.worldBorder = this.world.getWorldBorder();
        this.worldBorder.setCenter(0.0D, 0.0D);
        this.netherBorder = Bukkit.getWorlds().get(1).getWorldBorder();
        this.netherBorder.setCenter(0.0D, 0.0D);
        this.setupWorldBorder(2000, 0, true, false);
        
        this.world.getEntities().forEach(org.bukkit.entity.Entity::remove);
        this.status = Status.STARTING;
        
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
    }

    @Override
    public void startGame()
    {
        super.startGame();

        int i = 0;

        for(ArenaPlayer arenaPlayer : this.gamePlayers.values())
        {
            Player player = arenaPlayer.getPlayerIfOnline();
            
            if(this.arenaType == ArenaType.TEAM)
            {
                if(!arenaPlayer.hasTeam())
                {
                    for(ArenaTeam team : UHC.getPlugin().getArena().getTeamAlive())
                    {
                        if(!team.isFull() && !team.isLocked())
                        {
                            team.join(arenaPlayer);
                            break;
                        }
                    }

                    if(!arenaPlayer.hasTeam())
                        this.gameManager.kickPlayer(player, ChatColor.RED + "Aucune équipe était apte à vous reçevoir, vous avez été réenvoyé dans le lobby.");
                }
            }
                        
            this.setupPlayer(player.getPlayer());

            player.getPlayer().setFireTicks(0);

            if(this.arenaType == ArenaType.TEAM)
                player.teleport(arenaPlayer.getTeam().getSpawnLocation());
            else
                player.teleport(this.spawns.get(i));

            player.setGameMode(GameMode.SURVIVAL);
            player.setExp(0.0F);
            player.setLevel(0);
            
            UHC.getPlugin().closeGui(player);

            i++;
        }
        
        if(this.arenaType == ArenaType.TEAM)
        {
            this.arenaTeams.stream().filter(ArenaTeam::isEmpty).forEach(this.arenaTeams::remove);
        }
                
        this.episode = 1;
        this.minutesLeft = 20;
        this.secondsLeft = 0;
                
        UHC.getPlugin().getSpawnBlock().remove();
        
        this.firstsTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {
            private int seconds = 60;
            
            @Override
            public void run()
            {
                this.seconds--;

                for(Player player : Bukkit.getOnlinePlayers())
                    ActionBarAPI.sendMessage(player, ChatColor.RED + "Les dégats seront activés dans " + this.seconds + " seconde" + (this.seconds > 1 ? "s" : "") + " !");
                
                if(this.seconds == 0)
                {
                    firstMinute = true;

                    Bukkit.broadcastMessage(Messages.damageActivated.toString());

                    for(Player player : Bukkit.getOnlinePlayers())
                        ActionBarAPI.sendPermanentMessage(player, ChatColor.RED + "Le PvP sera activé au jour 2 !");

                    startGameCallback();
                }
            } 
        }, 20L, 20L);
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), () ->
        {
            this.updateScoreboard();

            for (Player player : Bukkit.getOnlinePlayers())
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);

            this.secondsLeft--;

            if (this.secondsLeft == -1)
            {
                this.minutesLeft--;
                this.secondsLeft = 59;

                if (this.episode == 8 && this.minutesLeft == 10)
                {
                    if (this.animatedBorders)
                        this.setupWorldBorder(50, 60 * 2, true, true);
                }
                else if (episode == 8 && minutesLeft == 8) {
                    Bukkit.broadcastMessage(Messages.reduced.toString().replace("${COORDS}", "-25 25"));
                }
                else if (episode == 8 && minutesLeft == 1) {
                    EndTimer endTimer = new EndTimer(this);
                    endTimer.start();
                }
            }

            if (this.minutesLeft == -1)
            {
                this.minutesLeft = 20;
                this.secondsLeft = 0;

                Bukkit.broadcastMessage(ChatColor.AQUA + "-------- Fin jour " + this.episode + " --------");

                this.easterEggManager.nextEpisode();
                this.episode++;

                if (this.episode == 2)
                {
                    this.firstTenMinutes = true;
                    Bukkit.broadcastMessage(Messages.pvpActivated.toString());

                    for(Player player : Bukkit.getOnlinePlayers())
                        ActionBarAPI.removeMessage(player);
                }
                else if (episode == 4)
                {
                    if (this.animatedBorders)
                        this.setupWorldBorder(200, 60 * 60, true, true);
                }
                else if (episode == 7)
                {
                    Bukkit.broadcastMessage(Messages.reduced.toString().replace("${COORDS}", "-100 100"));
                }
                else if (episode == 8)
                {
                    Bukkit.broadcastMessage(Messages.endOfGameAt.toString());
                }
            }
        }, 20L, 20L);
                
        if(this.arenaType == ArenaType.TEAM)
            Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[CONSEIL] Seulement vos alliés verront vos messages. Pour parler publiquement, précédez votre message de '/all'");
    }
    
    public void startGameCallback()
    {
        Bukkit.getScheduler().cancelTask(this.firstsTimer);
    }

    @Override
    public void handleLogin(Player player)
    {
        super.handleLogin(player);

        player.teleport(new Location(this.world, 0.5D, 200, 0.5D));

        this.objective.addReceiver(player);
        this.setupPlayer(player);

        if(this.arenaType == ArenaType.TEAM)
            player.getInventory().setItem(0, this.getSelectTeamItem());

        player.getInventory().setItem(8, this.coherenceMachine.getLeaveItem());
        player.setScoreboard(this.board);

        if(this.arenaType == ArenaType.TEAM)
            player.sendMessage(Messages.warningTeam.toString());

        this.gameManager.refreshArena();
    }

    @Override
    public void handleLogout(Player player)
    {
        super.handleLogout(player);
        this.objective.removeReceiver(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    @Override
    public void handleReconnect(Player player)
    {
        super.handleReconnect(player);

        player.setScoreboard(this.board);
        player.setGameMode(GameMode.SURVIVAL);
        this.objective.addReceiver(player);
    }

    @Override
    public void handleReconnectTimeOut(Player player)
    {
        super.handleReconnectTimeOut(player);

        if(this.disconnectLocation.containsKey(player.getUniqueId()) && this.disconnectInventory.containsKey(player.getUniqueId()))
        {
            for(ItemStack stack : this.disconnectInventory.get(player.getUniqueId()).getContents())
            {
                if(stack != null && stack.getType() != Material.AIR)
                    this.world.dropItemNaturally(this.disconnectLocation.get(player.getUniqueId()), stack);
            }

            for(ItemStack stack : this.disconnectInventory.get(player.getUniqueId()).getArmorContents())
            {
                if(stack != null && stack.getType() != Material.AIR)
                    this.world.dropItemNaturally(this.disconnectLocation.get(player.getUniqueId()), stack);
            }
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
                Titles.sendTitle(player, 0, 100, 20, ChatColor.DARK_RED + "Attention !", ChatColor.RED + "La bordure de map se rapproche :)");
            }
            
            Bukkit.broadcastMessage(Messages.reducing.toString().replace("${COORDS}", "-" + distance + " " + distance));
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
                ex.printStackTrace();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set " + distance + " " + time);
            }
        }
    }
    
    public void finishTeam(ArenaTeam team)
    {
        if(team != null)
        {
            StringBuilder players = new StringBuilder();
                                    
            for(UUID player : team.getPlayers())
            {
                ArenaPlayer arenaPlayer = this.getPlayer(player);

                this.increaseStat(player, "wins", 1);
                arenaPlayer.addCoins(CoinsUtils.getWinCoins(this.episode), "Victoire");
                arenaPlayer.addStars(this.episode * 2, "Victoire");
                
                players.append(PlayerUtils.getColoredFormattedPlayerName(Bukkit.getPlayer(player))).append(ChatColor.GRAY).append(", ");
            }

            ArrayList<String> winTemplateLines = new ArrayList<>();
            winTemplateLines.add(ChatUtils.getCenteredText(ChatColor.GREEN + "Gagnant" + ChatColor.GRAY + " - " + ChatColor.RESET + "Equipe " + team.getChatColor() + team.getName()));
            winTemplateLines.add(players.substring(0, players.length() - 2));

            this.coherenceMachine.getTemplateManager().getWinMessageTemplate().execute(winTemplateLines);
        }
        
        GameUtils.broadcastSound(Sound.WITHER_DEATH);
        this.handleGameEnd();
    }
    
    public void finishSolo(ArenaPlayer player)
    {         
        if(player != null)
        {
            ArenaPlayer arenaPlayer = this.getPlayer(player.getUUID());

            this.increaseStat(player.getUUID(), "wins", 1);
            arenaPlayer.addCoins(CoinsUtils.getWinCoins(this.episode), "Victoire");
            arenaPlayer.addStars(this.episode * 2, "Victoire");

            this.coherenceMachine.getTemplateManager().getPlayerWinTemplate().execute(player.getPlayerIfOnline());
        }
        
        GameUtils.broadcastSound(Sound.WITHER_DEATH);
        this.handleGameEnd();
    }
    
    public void updateScoreboard()
    {
        int team = 3;
        
        this.objective.setLine(0, ChatColor.GRAY + "Jour: " + ChatColor.WHITE + this.episode);
        this.objective.setLine(1, ChatColor.WHITE + "");
        this.objective.setLine(2, ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + this.getInGamePlayers().size());
        
        if(this.arenaType == ArenaType.TEAM)
        {
            this.objective.setLine(3, ChatColor.GRAY + "Equipes: " + ChatColor.WHITE + this.getTeamAlive().size());
            team = 4;
        }
        
        this.objective.setLine(team, ChatColor.GRAY + "");
        this.objective.setLine(team + 1, ChatColor.GRAY + "Bordure:");
        this.objective.setLine(team + 2, ChatColor.WHITE + "-" + String.valueOf(((int) this.worldBorder.getSize()) / 2) + " +" + String.valueOf(((int) this.worldBorder.getSize()) / 2));
        this.objective.setLine(team + 3, ChatColor.RED + "");
        this.objective.setLine(team + 4, ChatColor.WHITE + this.formatter.format(this.minutesLeft) + ":" + this.formatter.format(this.secondsLeft));
    
        this.objective.updateLines();
    }
    
    public void lose(UUID player)
    {
        GameUtils.broadcastSound(Sound.WITHER_SPAWN);
        this.increaseStat(player, "deaths", 1);
        
        if(Bukkit.getPlayer(player) != null)
        {
            Player playerQuited = Bukkit.getPlayer(player);

            if(playerQuited.getKiller() != null)
            {
                Player killer = playerQuited.getKiller();

                if(!killer.getUniqueId().equals(playerQuited.getUniqueId()))
                {
                    increaseStat(killer.getUniqueId(), "kills", 1);
                    this.getPlayer(killer.getUniqueId()).addCoins(CoinsUtils.getKillCoins(this.episode), "Meurtre d'un joueur");
                }
            }
        }

        this.getPlayer(player).setSpectator();
        
        if(this.arenaType == ArenaType.TEAM)
        {
            this.getPlayer(player).getTeam().lose(this.getPlayer(player));
                
            if(this.getInGamePlayers().size() == this.getPlayer(player).getTeam().getPlayers().size())
            {
                this.finishTeam(this.getPlayer(player).getTeam());
            }
        }
        else
        {
            if(this.getInGamePlayers().size() == 1)
            {
                this.finishSolo(this.getInGamePlayers().values().iterator().next());
            }
        }
    }
    
    public void loseTeam(ArenaTeam team)
    {
        GameUtils.broadcastMessage(Messages.teamEliminated.toString().replace("${TEAM}", team.getChatColor() + team.getName() + ChatColor.RESET));
        this.arenaTeams.remove(team);
        
        if(this.arenaTeams.size() == 1)
            this.finishTeam(this.arenaTeams.get(0));
    }
    
    public void createTeam(ArenaTeam team)
    {
        this.arenaTeams.add(team);
    }
    
    public void removeAdminTeam()
    {
        ArrayList<ArenaTeam> teams = new ArrayList<>(this.arenaTeams);

        teams.stream().filter(team -> team.getIcon().getType() == Material.COOKIE).forEach(this.arenaTeams::remove);
    }

    public ArenaType getArenaType()
    {
        return this.arenaType;
    }

    public ArrayList<ArenaTeam> getTeamAlive()
    {
        return this.arenaTeams;
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

    public int getMaxPlayersInTeam()
    {
        return this.maxPlayersInTeam;
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

    public EasterEggManager getEasterEggManager()
    {
        return this.easterEggManager;
    }
    
    public ObjectiveSign getObjectiveSign()
    {
        return this.objective;
    }

    public boolean isGameStarted()
    {
        return this.status == Status.IN_GAME;
    }

    public boolean isFirstMinute()
    {
        return this.firstMinute;
    }
    
    public boolean isFirstTenMinutes()
    {
        return this.firstTenMinutes;
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
