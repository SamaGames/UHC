package net.samagames.uhc.arena;

import net.samagames.tools.chat.FancyMessage;
import net.samagames.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;

public class ArenaTeam
{
    private final String originName;
    private final int number;
    private final int maxPlayers;
    private final ChatColor color;
    private final ItemStack icon;
    private final double spawnX, spawnZ;
    private final ArrayList<UUID> players;
    private final ArrayList<UUID> invited;
    private final Team team;
    private String name;
    private boolean locked;
    private UUID nameChanger;
    
    public ArenaTeam(ArenaCommon arena, int number, int maxPlayers, String name, ChatColor color, ItemStack icon, double spawnX, double spawnZ)
    {
        this.originName = name;
        this.number = number;
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
        
        this.players = new ArrayList<>();
        this.invited = new ArrayList<>();
        
        Scoreboard board = arena.getScoreboard();
        
        if(this.name.contains("\u00a7"))
        {
            board.registerNewTeam(ChatColor.stripColor(this.name));
            this.team = board.getTeam(ChatColor.stripColor(this.name));
        }
        else
        {
            board.registerNewTeam(this.name);
            this.team = board.getTeam(this.name);
        }
        
        this.team.setDisplayName(this.name);
        this.team.setCanSeeFriendlyInvisibles(true);
        this.team.setPrefix(this.color + "");
        this.team.setSuffix(ChatColor.RESET + "");        
    }
    
    public void join(ArenaPlayer player)
    {
        this.players.add(player.getUUID());
        this.team.addPlayer(player.getPlayerIfOnline());
        player.setTeam(this);
    }
    
    public void invite(ArenaPlayer inviter, ArenaPlayer player)
    {
        this.invited.add(player.getUUID());

        new FancyMessage("Vous avez été invité dans l'équipe " + this.name + " par " + inviter.getPlayerIfOnline().getName() + " ")
                .color(ChatColor.GOLD)
                .style(ChatColor.BOLD)
                .then("[Rejoindre]")
                .color(ChatColor.GREEN)
                .style(ChatColor.BOLD)
                .command("/uhc join " + this.number + " " + player.getPlayerIfOnline().getName())
                .send(player.getPlayerIfOnline());
    }
    
    public void removeInvite(UUID uuid)
    {
        if(this.invited.contains(uuid))
            this.invited.remove(uuid);
    }
    
    public void leave(ArenaPlayer player)
    {
        this.players.remove(player.getUUID());
        this.team.removePlayer(player.getPlayerIfOnline());
        player.setTeam(null);
        
        if(player.getUUID().equals(this.nameChanger))
            this.name = this.originName;
        
        if(this.isEmpty())
            this.locked = false;
    }
    
    public void changeName(UUID uuid, String name)
    {
        this.name = name;
        this.nameChanger = uuid;
    }
    
    public void remove(UUID uuid)
    {
        this.players.remove(uuid);
    }
    
    public void lose(ArenaPlayer player)
    {
        this.remove(player.getUUID());
        this.team.removePlayer(player.getPlayerIfOnline());
        
        if(this.players.isEmpty())
        {
            UHC.getPlugin().getArena().loseTeam(this);
        }
    }
    
    public void chat(ArenaPlayer player, String message)
    {
        this.players.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).forEach(uuid -> Bukkit.getPlayer(uuid).sendMessage(this.color + "[Equipe] " + ChatColor.RESET + player.getPlayerIfOnline().getName() + ": " + message));
    }
    
    public void setLocked(boolean bool)
    {
        this.locked = bool;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public int getNumber()
    {
        return this.number;
    }
    
    public ChatColor getChatColor()
    {
        return this.color;
    }
    
    public Team getScoreboardTeam()
    {
        return this.team;
    }
    
    public ItemStack getIcon()
    {
        return this.icon;
    }
    
    public double getSpawnX()
    {
        return this.spawnX;
    }
    
    public double getSpawnZ()
    {
        return this.spawnZ;
    }
    
    public Location getSpawnLocation()
    {
        return new Location(Bukkit.getWorlds().get(0), this.spawnX, 250, this.spawnZ);
    }
    
    public ArrayList<UUID> getPlayers()
    {
        return this.players;
    }
    
    public boolean canJoin()
    {
        return (!this.isLocked() && !this.isFull());
    }
    
    public boolean hasPlayer(UUID uuid)
    {
        return this.players.contains(uuid);
    }
    
    public boolean isFull()
    {
        return this.players.size() == this.maxPlayers;
    }
    
    public boolean isLocked()
    {
        return this.locked;
    }
    
    public boolean isEmpty()
    {
        return this.players.isEmpty();
    }
    
    public boolean isInvited(UUID uuid)
    {
        return this.invited.contains(uuid);
    }
}
