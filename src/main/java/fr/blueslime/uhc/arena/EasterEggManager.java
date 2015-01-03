package fr.blueslime.uhc.arena;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.utils.ParticleEffect;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EasterEggManager
{
    private final String maiteTag;
    private final ArrayList<ChatColor> rainbow;
    private final ParticleEffect heart;
    private int loopID;
    private int rainbowSlot;
    private int count;
    
    public EasterEggManager()
    {        
        this.maiteTag = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Maïté" + ChatColor.GOLD + "] " + ChatColor.RESET;
        this.heart = ParticleEffect.HEART;
        this.count = 0;
        this.rainbowSlot = 0;
        
        this.rainbow = new ArrayList<>();
        this.rainbow.add(ChatColor.DARK_RED);
        this.rainbow.add(ChatColor.RED);
        this.rainbow.add(ChatColor.GOLD);
        this.rainbow.add(ChatColor.YELLOW);
        this.rainbow.add(ChatColor.GREEN);
        this.rainbow.add(ChatColor.DARK_AQUA);
        this.rainbow.add(ChatColor.DARK_BLUE);
        this.rainbow.add(ChatColor.DARK_AQUA);
        this.rainbow.add(ChatColor.GREEN);
        this.rainbow.add(ChatColor.YELLOW);
        this.rainbow.add(ChatColor.GOLD);
        this.rainbow.add(ChatColor.RED);
        this.rainbow.add(ChatColor.DARK_RED);
    }
    
    public void start()
    {        
        this.loopID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(UHC.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                update();
            }
        }, 0, 1);
    }
    
    public void kill()
    {
        Bukkit.getScheduler().cancelTask(this.loopID);
    }
    
    public void update()
    {
        if(UHC.getPlugin().getArena().isAdminTeamExist())
        {
            ArenaTeam team = UHC.getPlugin().getArena().getAdminTeam();
            ParticleEffect note = ParticleEffect.NOTE;
            
            for(UUID uuid : team.getPlayers())
            {
                note.display(0.5F, 0.5F, 0.5F, 0.25F, 1, Bukkit.getPlayer(uuid).getLocation(), 160.0);
            }
            
            this.count++;
            
            if(this.count == 10)
            {
                this.count = 0;
                this.rainbowSlot++;
                
                if(this.rainbowSlot == this.rainbow.size())
                    this.rainbowSlot = 0;
                
                team.getScoreboardTeam().setPrefix(this.rainbow.get(this.rainbowSlot) + "");
            }
        }
        
        if(!UHC.getPlugin().getArena().isGameStarted())
        {
            if(Bukkit.getPlayer(UUID.fromString("dfd16cea-d6d8-4f51-aade-8c7ad157c93f")) != null)
            {
                this.heart.display(0.5F, 0.5F, 0.5F, 0.25F, 1, Bukkit.getPlayer(UUID.fromString("dfd16cea-d6d8-4f51-aade-8c7ad157c93f")).getLocation(), 160.0);
            }
        }
    }
    
    public void nextEpisode()
    {
        if(UHC.getPlugin().getArena().isAdminTeamExist())
        {
            ItemStack cookie = new ItemStack(Material.COOKIE, 1);
            ItemMeta cookieMeta = cookie.getItemMeta();
            cookieMeta.setDisplayName(ChatColor.GOLD + "Le cookie de Maïté");
            cookie.setItemMeta(cookieMeta);

            for(ArenaPlayer player : UHC.getPlugin().getArena().getArenaPlayers())
            {
                player.getPlayer().getInventory().addItem(cookie);
                player.getPlayer().sendMessage(this.maiteTag + ChatColor.YELLOW + "Sur la qualité, on ne me la fait pas. Goute moi ça !");
            }
        }
    }
}
