package fr.blueslime.uhc.events;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon.ArenaType;
import fr.blueslime.uhc.arena.ArenaPlayer;
import fr.blueslime.uhc.gui.GuiSelectTeam;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class UHCSignChangeEvent implements Listener
{
    @EventHandler
    public void event(SignChangeEvent event)
    {
        if(UHC.getPlugin().getArena().getArenaType() == ArenaType.TEAM)
        {
            if(!UHC.getPlugin().getArena().isGameStarted())
            {
                ArenaPlayer player = UHC.getPlugin().getArena().getPlayer(event.getPlayer().getUniqueId());
                event.getBlock().setType(Material.AIR);

                if(!event.getLine(0).equals(""))
                {
                    if(StringUtils.containsIgnoreCase("Maité", event.getLine(0)) ||
                        StringUtils.containsIgnoreCase("Maite", event.getLine(0)) ||
                        StringUtils.containsIgnoreCase("Maïté", event.getLine(0)) ||
                        StringUtils.containsIgnoreCase("Maïte", event.getLine(0))
                    )
                    {
                        event.getPlayer().sendMessage(Messages.dontTouchHer.toString());
                    }
                    else
                    {
                        player.getTeam().changeName(player.getUUID(), event.getLine(0));
                        event.getPlayer().sendMessage(Messages.teamNameChanged.toString().replace("${NAME}", event.getLine(0)));
                        UHC.getPlugin().openGui(event.getPlayer(), new GuiSelectTeam());
                    }
                }
                else
                {
                    event.getPlayer().sendMessage(Messages.lineEmpty.toString());
                }
            }
        }
    }
}
