package fr.blueslime.uhc;

import net.samagames.gameapi.GameAPI;
import org.bukkit.ChatColor;

public class Messages
{
    public static final String PLUGIN_TAG = GameAPI.getCoherenceMachine("UHC").getGameTag();
    
    public static final String teamEliminated = PLUGIN_TAG + "L'équipe ${TEAM} est éliminée !";
    public static final String playerQuitted = PLUGIN_TAG + "${PLAYER} s'est enfui du jeu :(";
    public static final String reducting = PLUGIN_TAG + "Réduction progressive de la bordure en cours en ${COORDS}. Courez !";
    public static final String reducted = PLUGIN_TAG + "La bordure a finie d'être réduite en ${COORDS} !";
    public static final String damageActivated = PLUGIN_TAG + ChatColor.RED + "Les dégats sont maintenant activés !";
    public static final String pvpActivated = PLUGIN_TAG + ChatColor.RED + "Les combats entre joueurs sont maintenant activés !";
    public static final String teamFull = PLUGIN_TAG + ChatColor.RED + "L'équipe choisie est pleine :/";
    public static final String teamJoined = PLUGIN_TAG + ChatColor.YELLOW + "Vous êtes entré dans l'équipe ${TEAM}" + ChatColor.YELLOW + " !";
    public static final String warningTeam = ChatColor.RED + "" + ChatColor.BOLD + "Les équipes non programmées sont interdites ! Préparez votre équipe à l'avance !";
    public static final String noTeam = PLUGIN_TAG + ChatColor.RED + "Vous devez avoir une équipe pour pouvoir utiliser cette fonction !";
    public static final String teamNameChanged = PLUGIN_TAG + ChatColor.YELLOW + "Le nom de l'équipe a été changé pour : ${NAME} " + ChatColor.YELLOW + "!";
    public static final String needVIP = PLUGIN_TAG + ChatColor.RED + "Vous devez être VIP pour pouvoir utiliser cette fonction !";
    public static final String lineEmpty = PLUGIN_TAG + ChatColor.RED + "Vous ne pouvez pas donner un nom d'équipe vide !";
    public static final String adminOnly = PLUGIN_TAG + ChatColor.RED + "Cette équipe est réservée au développeur du jeu !";
    public static final String teamLocked = PLUGIN_TAG + ChatColor.RED + "Votre équipe est maintenant fermée !";
    public static final String teamUnlocked = PLUGIN_TAG + ChatColor.GREEN + "Votre équipe est maintenant ouverte !";
    public static final String inviteSuccessful = PLUGIN_TAG + ChatColor.GREEN + "Le joueur a été invité !";
    public static final String playersAvailable = PLUGIN_TAG + ChatColor.YELLOW + "Vous pouvez inviter les joueurs suivants :";
    public static final String teamLockedJoin = PLUGIN_TAG + ChatColor.RED + "L'équipe choisie est fermée !";
    public static final String teamLeaved = PLUGIN_TAG + ChatColor.GREEN + "Vous avez quitté l'équipe !";
    public static final String dontTouchHer = PLUGIN_TAG + ChatColor.RED + ChatColor.BOLD + "NE TOUCHE PAS A MAITE !";
    public static final String endOfGameAt = PLUGIN_TAG + ChatColor.RED + "Fin de la partie forcée à la fin de l'épisode !";
    public static final String endOfGameIn = PLUGIN_TAG + ChatColor.RED + "Fin de la partie forcée dans " + ChatColor.AQUA + "${TIME}";
    public static final String wrongGameType = PLUGIN_TAG + ChatColor.RED + "Cette commande ne correspond pas à ce type de jeu !";
    public static final String blockPlaceRefused = PLUGIN_TAG + ChatColor.RED + "Vous ne pouvez pas poser ce bloc ici !";
    public static final String lavaPlaceRefused = PLUGIN_TAG + ChatColor.RED + "Vous ne pouvez pas poser de la lave aussi près d'un portail !";
    public static final String reconnected = PLUGIN_TAG + ChatColor.GREEN + "${PLAYER} est revenu en jeu :) !";
    public static final String disconnected = PLUGIN_TAG + ChatColor.RED + "${PLAYER} s'est déconnecté ! Il a 3 minutes pour revenir.";
    public static final String timeOut = PLUGIN_TAG + ChatColor.RED + "${PLAYER} s'est déconnecté et n'est pas revenu après 3 minutes :(";
}