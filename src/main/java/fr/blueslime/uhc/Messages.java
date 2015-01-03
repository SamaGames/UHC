package fr.blueslime.uhc;

import net.samagames.gameapi.GameAPI;
import org.bukkit.ChatColor;

public class Messages
{
    public static final String PLUGIN_TAG = GameAPI.getCoherenceMachine("KTP").getGameTag();
    public static final String DEBUG_TAG = PLUGIN_TAG + ChatColor.GRAY + "[" + ChatColor.RED + "DEBUG" + ChatColor.GRAY + "] " + ChatColor.RESET;
    
    public static String teamEliminated = PLUGIN_TAG + "L'équipe ${TEAM} est éliminée !";
    public static String playerQuitted = PLUGIN_TAG + "${PLAYER} s'est enfui du jeu :(";
    public static String reducting = PLUGIN_TAG + "Réduction progressive de la bordure en cours en ${COORDS}. Courez !";
    public static String reducted = PLUGIN_TAG + "La bordure a finie d'être réduite en ${COORDS} !";
    public static String damageActivated = PLUGIN_TAG + ChatColor.RED + "Les dégats sont maintenant activés !";
    public static String pvpActivated = PLUGIN_TAG + ChatColor.RED + "Les combats entre joueurs sont maintenant activés !";
    public static String teamFull = PLUGIN_TAG + ChatColor.RED + "L'équipe choisie est pleine :/";
    public static String teamJoined = PLUGIN_TAG + ChatColor.YELLOW + "Vous êtes entré dans l'équipe ${TEAM}" + ChatColor.YELLOW + " !";
    public static String warningTeam = ChatColor.RED + "" + ChatColor.BOLD + "Les équipes non programmées sont interdites ! Préparez votre équipe à l'avance !";
    public static String noTeam = PLUGIN_TAG + ChatColor.RED + "Vous devez avoir une équipe pour pouvoir utiliser cette fonction !";
    public static String teamNameChanged = PLUGIN_TAG + ChatColor.YELLOW + "Le nom de l'équipe a été changé pour : ${NAME} " + ChatColor.YELLOW + "!";
    public static String needVIP = PLUGIN_TAG + ChatColor.RED + "Vous devez être VIP pour pouvoir utiliser cette fonction !";
    public static String lineEmpty = PLUGIN_TAG + ChatColor.RED + "Vous ne pouvez pas donner un nom d'équipe vide !";
    public static String spectator = PLUGIN_TAG + ChatColor.GOLD + ChatColor.BOLD + "Vous etes spectateur. Vous pouvez vous téléporter aux joueurs via un clic molette de votre souris.";
    public static String adminOnly = PLUGIN_TAG + ChatColor.RED + "Cette équipe est réservée au développeur du jeu !";
    public static String disconnected = PLUGIN_TAG + ChatColor.RED + "${PLAYER} s'est déconnecté et n'est pas revenu après 10 minutes :(";
    public static String teamLocked = PLUGIN_TAG + ChatColor.RED + "Votre équipe est maintenant fermée !";
    public static String teamUnlocked = PLUGIN_TAG + ChatColor.GREEN + "Votre équipe est maintenant ouverte !";
    public static String inviteSuccessful = PLUGIN_TAG + ChatColor.GREEN + "Le joueur a été invité !";
    public static String playersAvailable = PLUGIN_TAG + ChatColor.YELLOW + "Vous pouvez inviter les joueurs suivants :";
    public static String teamLockedJoin = PLUGIN_TAG + ChatColor.RED + "L'équipe choisie est fermée !";
    public static String teamLeaved = PLUGIN_TAG + ChatColor.GREEN + "Vous avez quitté l'équipe !";
    public static String dontTouchHer = PLUGIN_TAG + ChatColor.RED + ChatColor.BOLD + "NE TOUCHE PAS A MAITE !";
    public static String endOfGameAt = PLUGIN_TAG + ChatColor.RED + "Fin de la partie forcée à la fin de l'épisode !";
    public static String endOfGameIn = PLUGIN_TAG + ChatColor.RED + "Fin de la partie forcée dans " + ChatColor.AQUA + "${TIME}";
    public static String wrongGameType = PLUGIN_TAG + ChatColor.RED + "Cette commande ne correspond pas à ce type de jeu !";
}