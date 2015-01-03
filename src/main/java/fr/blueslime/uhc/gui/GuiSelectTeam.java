package fr.blueslime.uhc.gui;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
import fr.blueslime.uhc.arena.ArenaPlayer;
import fr.blueslime.uhc.arena.ArenaTeam;
import fr.blueslime.uhc.utils.ItemUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.TileEntitySign;
import net.samagames.permissionsbukkit.PermissionsBukkit;
import net.samagames.utils.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiSelectTeam implements Gui
{
    private Inventory inventory = null;
    private Field signField;
    private Field isEditable;
    private Method openSign;
    private Method setEditor;
    private Method getHandle;
    
    @Override
    public void display(Player player)
    {
        this.inventory = Bukkit.getServer().createInventory(null, 54, "Sélection d'équipe");
        
        try
        {
            this.signField = CraftSign.class.getDeclaredField("sign");
            this.signField.setAccessible(true);
            this.isEditable = TileEntitySign.class.getDeclaredField("isEditable");
            this.isEditable.setAccessible(true);
            this.getHandle = CraftPlayer.class.getDeclaredMethod("getHandle", new Class[0]);
            this.openSign = EntityHuman.class.getDeclaredMethod("openSign", new Class[] { TileEntitySign.class });
            this.setEditor = TileEntitySign.class.getDeclaredMethod("a", new Class[] { EntityHuman.class });
        }
        catch (NoSuchFieldException | SecurityException | NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }
        
        int last = 10;
        
        for(ArenaTeam team : UHC.getPlugin().getArena().getTeamAlive())
        {
            String name;
            
            if(team.getIcon().getType() == Material.COOKIE)
            {
                name = team.getChatColor() + "Equipe " + team.getName() + team.getChatColor() + " [" + team.getPlayers().size() + "/∞]";
            }
            else
            {
                name = team.getChatColor() + "Equipe " + team.getName() + " [" + team.getPlayers().size() + "/" + UHC.getPlugin().getArena().getMaxPlayersInTeam() + "]";
            }
            
            ArrayList<String> lores = new ArrayList<>();
            
            if(team.isLocked())
            {
                lores.add(ChatColor.RED + "L'équipe est fermée !");
                lores.add("");
            }
            
            for(UUID uuid : team.getPlayers())
            {
                if(Bukkit.getPlayer(uuid) != null)
                    lores.add(team.getChatColor() + " - " + Bukkit.getPlayer(uuid).getName());
                else
                    team.remove(uuid);
            }
            
            ItemUtils.createDisplay(team.getIcon(), this.inventory, last, name, lores, false);
            
            if(last == 16)
            {
                last = 19;
            }
            else
            {
                last++;
            }
        }
        
        ItemUtils.createDisplay(Material.ARROW, this.inventory, 1, 31, "Sortir de l'équipe", null, false);
            
        ArrayList<String> lores = new ArrayList<>();
        lores.add(ChatColor.GREEN + "Réservé aux VIP :)");
        
        ItemUtils.createDisplay(Material.BARRIER, this.inventory, 1, 39, "Ouvrir/Fermer l'équipe", lores, false);
        ItemUtils.createDisplay(Material.BOOK_AND_QUILL, this.inventory, 1, 40, "Changer le nom de l'équipe", lores, false);
        ItemUtils.createDisplay(Material.FEATHER, this.inventory, 1, 41, "Inviter un joueur", lores, false);

        player.openInventory(this.inventory);
    }

    @Override
    public void onClic(final Player player, ItemStack stack)
    {
        ArenaCommon arena = UHC.getPlugin().getArena();
        
        if(stack.getType() == Material.WOOL)
        {
            short data = stack.getDurability();
            
            for(ArenaTeam team : arena.getTeamAlive())
            {
                if(team.getIcon().getDurability() == data)
                {
                    if(!team.isLocked())
                    {
                        if(team.canJoin())
                        {
                            if(arena.getPlayer(player.getUniqueId()).hasTeam())
                                arena.getPlayer(player.getUniqueId()).getTeam().leave(arena.getPlayer(player.getUniqueId()));

                            team.join(UHC.getPlugin().getArena().getPlayer(player.getUniqueId()));
                            player.sendMessage(Messages.teamJoined.replace("${TEAM}", team.getChatColor() + team.getName()) + ChatColor.YELLOW);
                        }
                        else
                        {
                            player.sendMessage(Messages.teamFull);
                        }
                    }
                    else
                    {
                        player.sendMessage(Messages.teamLockedJoin);
                    }
                    
                    break;
                }
            }
            
            UHC.getPlugin().openGui(player, new GuiSelectTeam());
        }
        else if(stack.getType() == Material.COOKIE)
        {
            player.sendMessage(Messages.adminOnly);
        }
        else if(stack.getType() == Material.BOOK_AND_QUILL)
        {
            if(PermissionsBukkit.hasPermission(player, "uhc.teamname"))
            {
                final ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if(aPlayer.hasTeam())
                {
                    if(aPlayer.getTeam().getIcon().getType() != Material.COOKIE)
                    {
                        final Block block = aPlayer.getPlayer().getWorld().getBlockAt(0, 250, aPlayer.getTeam().getNumber());
                        block.setTypeIdAndData(Material.SIGN_POST.getId(), (byte) 2, false);

                        Sign sign = (Sign) block.getState();
                        sign.setLine(0, aPlayer.getTeam().getName());
                        sign.update(true);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getPlugin(), new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    final Object signTile = signField.get(block.getState());
                                    final Object entityPlayer = getHandle.invoke(player, new Object[0]);

                                    Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getPlugin(), new Runnable()
                                    {
                                        public void run()
                                        {
                                            try
                                            {
                                                openSign.invoke(entityPlayer, new Object[] { signTile });
                                                setEditor.invoke(signTile, new Object[] { entityPlayer });
                                                isEditable.set(signTile, true);
                                            }
                                            catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex)
                                            {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }, 5L);
                                }
                                catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
                                {
                                    ex.printStackTrace();
                                }						
                            }
                        }, 1L);
                    }
                    else
                    {
                        player.sendMessage(Messages.dontTouchHer);
                    }
                }
                else
                {
                    player.sendMessage(Messages.noTeam);
                }
            }
            else
            {
                player.sendMessage(Messages.needVIP);
            }
        }
        else if(stack.getType() == Material.BARRIER)
        {
            if(PermissionsBukkit.hasPermission(player, "uhc.teamlock"))
            {
                ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if(aPlayer.hasTeam())
                {
                    if(aPlayer.getTeam().isLocked())
                    {
                        aPlayer.getTeam().setLocked(false);
                        player.sendMessage(Messages.teamUnlocked);
                    }
                    else
                    {
                        aPlayer.getTeam().setLocked(true);
                        player.sendMessage(Messages.teamLocked);
                    }
                }
                else
                {
                    player.sendMessage(Messages.noTeam);
                }
            }
            else
            {
                player.sendMessage(Messages.needVIP);
            }
        }
        else if(stack.getType() == Material.FEATHER)
        {
            if(PermissionsBukkit.hasPermission(player, "uhc.teaminvite"))
            {
                ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if(aPlayer.hasTeam())
                {
                    player.sendMessage(Messages.playersAvailable);
                    
                    for(ArenaPlayer aInvite : UHC.getPlugin().getArena().getArenaPlayers())
                    {
                        if(!aInvite.hasTeam())
                        {
                            new FancyMessage(" - " + aInvite.getPlayer().getName() + " ")
                                .color(ChatColor.GRAY)
                            .then("[Inviter]")
                                .color(ChatColor.GREEN)
                                .style(ChatColor.BOLD)
                                .command("/uhc invite " + player.getName() + " " + aInvite.getPlayer().getName())
                            .send(player);
                        }
                    }
                }
                else
                {
                    player.sendMessage(Messages.noTeam);
                }
            }
            else
            {
                player.sendMessage(Messages.needVIP);
            }
        }
        else if(stack.getType() == Material.ARROW)
        {
            ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());
            
            if(aPlayer.hasTeam())
            {
                aPlayer.getTeam().leave(aPlayer);
                player.sendMessage(Messages.teamLeaved);
            }
            else
            {
                player.sendMessage(Messages.noTeam);
            }
        }
    }

    @Override
    public Inventory getInventory()
    {
        return this.inventory;
    }
}
