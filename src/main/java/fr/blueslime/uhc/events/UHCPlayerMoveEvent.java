package fr.blueslime.uhc.events;

import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.utils.ModMessage;
import net.zyuiop.MasterBundle.MasterBundle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class UHCPlayerMoveEvent implements Listener
{
    @EventHandler
    public void event(PlayerMoveEvent event)
    {
        if(UHC.getPlugin().getArena().isFirst30Seconds())
        {
            // Tests de faux positifs
            if
            (
                    !event.getPlayer().isInsideVehicle() &&
                    !event.getPlayer().isDead() &&
                    event.getPlayer().getGameMode() == GameMode.SURVIVAL
            )
            {
                if
                (
                    event.getTo().getX() - event.getFrom().getX() > 1 || event.getFrom().getX() - event.getTo().getX() > 1 ||
                    event.getTo().getZ() - event.getFrom().getZ() > 1 || event.getFrom().getZ() - event.getTo().getZ() > 1
                )
                {
                    Location loc = event.getPlayer().getLocation();

                    if (loc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
                    {
                        event.getPlayer().teleport(loc);

                        ModMessage.broadcastModMessage(MasterBundle.getServerName(), "Le joueur '" + event.getPlayer().getName() + "' effectue des mouvements suspects !");
                        Bukkit.getLogger().severe("ALERT! " + event.getPlayer().getName() + " is doing suspicious movements!");
                    }
                }
            }
        }
    }
}
