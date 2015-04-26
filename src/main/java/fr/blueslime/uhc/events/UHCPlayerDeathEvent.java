package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
import net.samagames.gameapi.GameAPI;
import net.samagames.permissionsbukkit.PermissionsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class UHCPlayerDeathEvent implements Listener
{
    private int timer;
    
    @EventHandler
    public void event(PlayerDeathEvent event)
    {
        final Player deadPlayer = event.getEntity();
        ArenaCommon arena = UHC.getPlugin().getArena();
                
        if(arena.isGameStarted())
        {
            if(arena.hasPlayer(deadPlayer.getUniqueId()))
            {
                if(deadPlayer.isDead())
                {
                    arena.lose(deadPlayer.getUniqueId(), false);
                    event.getEntity().getLocation().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(Material.GOLDEN_APPLE, 1));
                    
                    if(!PermissionsBukkit.hasPermission(deadPlayer.getUniqueId(), "uhc.spectate"))
                    {
                        deadPlayer.getPlayer().sendMessage(ChatColor.RED + "Vous serez téléporté au lobby dans 20 secondes...");
                        
                        this.timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(), new Runnable()
                        {
                            int time;
                            
                            @Override
                            public void run()
                            {
                                time = time + 1;
                                
                                if(time == 20)
                                {
                                    deadPlayer.getPlayer().sendMessage(ChatColor.RED + "Merci d'avoir joué :)");
                                    callback(deadPlayer);
                                }
                            } 
                        }, 20L, 20L);
                    }
                }
            }
            else
            {
                event.setDeathMessage(null);
            }
        }
    }
    
    private void callback(Player deadPlayer)
    {
        Bukkit.getScheduler().cancelTask(this.timer);
        GameAPI.kickPlayer(deadPlayer.getPlayer());
    }
}
