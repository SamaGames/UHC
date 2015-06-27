package fr.blueslime.uhc.gui;

import fr.blueslime.uhc.Messages;
import fr.blueslime.uhc.UHC;
import fr.blueslime.uhc.arena.ArenaCommon;
import fr.blueslime.uhc.arena.ArenaPlayer;
import fr.blueslime.uhc.arena.ArenaTeam;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.TileEntitySign;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.AbstractGui;
import net.samagames.tools.chat.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R2.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class GuiSelectTeam extends AbstractGui
{
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
            this.openSign = EntityHuman.class.getDeclaredMethod("openSign", new Class[]{TileEntitySign.class});
            this.setEditor = TileEntitySign.class.getDeclaredMethod("a", new Class[]{EntityHuman.class});
        }
        catch (NoSuchFieldException | SecurityException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        int last = 10;

        for (ArenaTeam team : UHC.getPlugin().getArena().getTeamAlive())
        {
            String name;

            if (team.getIcon().getType() == Material.COOKIE)
                name = team.getChatColor() + "Equipe " + team.getName() + team.getChatColor() + " [" + team.getPlayers().size() + "/∞]";
            else
                name = team.getChatColor() + "Equipe " + team.getName() + " [" + team.getPlayers().size() + "/" + UHC.getPlugin().getArena().getMaxPlayersInTeam() + "]";

            ItemStack icon = team.getIcon();
            ArrayList<String> lores = new ArrayList<>();

            if (team.isLocked())
            {
                lores.add(ChatColor.RED + "L'équipe est fermée !");
                lores.add("");
            }

            for (UUID uuid : team.getPlayers())
            {
                if (Bukkit.getPlayer(uuid) != null)
                    lores.add(team.getChatColor() + " - " + Bukkit.getPlayer(uuid).getName());
                else
                    team.remove(uuid);
            }

            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(name);
            iconMeta.setLore(lores);
            icon.setItemMeta(iconMeta);

            this.setSlotData(icon, last, team.getName());

            if (last == 16)
                last = 19;
            else
                last++;
        }

        this.setSlotData("Sortir de l'équipe", Material.ARROW, 31, null, "quit");

        String[] lores = new String[] {
                ChatColor.GREEN + "Réservé aux VIP :)"
        };

        this.setSlotData("Ouvrir/Fermer l'équipe", Material.BARRIER, 39, lores, "toggle");
        this.setSlotData("Changer le nom de l'équipe", Material.BOOK_AND_QUILL, 40, lores, "change-team");
        this.setSlotData("Inviter un joueur", Material.FEATHER, 41, lores, "invite");

        player.openInventory(this.inventory);
    }

    @Override
    public void onClick(Player player, ItemStack stack, String action)
    {
        ArenaCommon arena = UHC.getPlugin().getArena();

        if (action.equals("toggle"))
        {
            if (SamaGamesAPI.get().getPermissionsManager().hasPermission(player, "uhc.teamlock"))
            {
                ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if (aPlayer.hasTeam())
                {
                    if (aPlayer.getTeam().isLocked())
                    {
                        aPlayer.getTeam().setLocked(false);
                        player.sendMessage(Messages.teamUnlocked.toString());
                    }
                    else
                    {
                        aPlayer.getTeam().setLocked(true);
                        player.sendMessage(Messages.teamLocked.toString());
                    }
                }
                else
                {
                    player.sendMessage(Messages.noTeam.toString());
                }
            }
            else
            {
                player.sendMessage(Messages.needVIP.toString());
            }
        }
        else if (action.equals("change-team"))
        {
            if (SamaGamesAPI.get().getPermissionsManager().hasPermission(player, "uhc.teamname"))
            {
                final ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if (aPlayer.hasTeam())
                {
                    if (aPlayer.getTeam().getIcon().getType() != Material.COOKIE)
                    {
                        final Block block = aPlayer.getPlayerIfOnline().getWorld().getBlockAt(0, 250, aPlayer.getTeam().getNumber());
                        block.setTypeIdAndData(Material.SIGN_POST.getId(), (byte) 2, false);

                        Sign sign = (Sign) block.getState();
                        sign.setLine(0, aPlayer.getTeam().getName());
                        sign.update(true);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getPlugin(), () ->
                        {
                            try
                            {
                                final Object signTile = signField.get(block.getState());
                                final Object entityPlayer = getHandle.invoke(player, new Object[0]);

                                Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getPlugin(), () ->
                                {
                                    try
                                    {
                                        openSign.invoke(entityPlayer, new Object[]{signTile});
                                        setEditor.invoke(signTile, new Object[]{entityPlayer});
                                        isEditable.set(signTile, true);
                                    }
                                    catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex)
                                    {
                                        ex.printStackTrace();
                                    }
                                }, 5L);
                            }
                            catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex)
                            {
                                ex.printStackTrace();
                            }
                        }, 1L);
                    }
                    else
                    {
                        player.sendMessage(Messages.dontTouchHer.toString());
                    }
                }
                else
                {
                    player.sendMessage(Messages.noTeam.toString());
                }
            }
            else
            {
                player.sendMessage(Messages.needVIP.toString());
            }
        }
        else if (action.equals("invite"))
        {
            if (SamaGamesAPI.get().getPermissionsManager().hasPermission(player, "uhc.teaminvite"))
            {
                ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if (aPlayer.hasTeam())
                {
                    player.sendMessage(Messages.playersAvailable.toString());

                    for (ArenaPlayer aInvite : UHC.getPlugin().getArena().getInGamePlayers().values())
                    {
                        if (!aInvite.hasTeam())
                        {
                            new FancyMessage(" - " + aInvite.getPlayerIfOnline().getName() + " ")
                                    .color(ChatColor.GRAY)
                                    .then("[Inviter]")
                                    .color(ChatColor.GREEN)
                                    .style(ChatColor.BOLD)
                                    .command("/uhc invite " + player.getName() + " " + aInvite.getPlayerIfOnline().getName())
                                    .send(player);
                        }
                    }
                }
                else
                {
                    player.sendMessage(Messages.noTeam.toString());
                }
            } else if (action.equals("quit"))
            {
                ArenaPlayer aPlayer = arena.getPlayer(player.getUniqueId());

                if (aPlayer.hasTeam())
                {
                    aPlayer.getTeam().leave(aPlayer);
                    player.sendMessage(Messages.teamLeaved.toString());
                }
                else
                {
                    player.sendMessage(Messages.noTeam.toString());
                }
            }
            else
            {
                if (stack.getType() == Material.WOOL)
                {
                    short data = stack.getDurability();

                    for (ArenaTeam team : arena.getTeamAlive())
                    {
                        if (team.getIcon().getDurability() == data)
                        {
                            if (!team.isLocked())
                            {
                                if (team.canJoin())
                                {
                                    if (arena.getPlayer(player.getUniqueId()).hasTeam())
                                        arena.getPlayer(player.getUniqueId()).getTeam().leave(arena.getPlayer(player.getUniqueId()));

                                    team.join(UHC.getPlugin().getArena().getPlayer(player.getUniqueId()));
                                    player.sendMessage(Messages.teamJoined.toString().replace("${TEAM}", team.getChatColor() + team.getName()) + ChatColor.YELLOW);
                                }
                                else
                                {
                                    player.sendMessage(Messages.teamFull.toString());
                                }
                            }
                            else
                            {
                                player.sendMessage(Messages.teamLockedJoin.toString());
                            }

                            break;
                        }
                    }

                    UHC.getPlugin().openGui(player, new GuiSelectTeam());
                }
                else if (stack.getType() == Material.COOKIE)
                {
                    player.sendMessage(Messages.adminOnly.toString());
                }
            }
        }
    }
}