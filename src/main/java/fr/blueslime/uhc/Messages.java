package fr.blueslime.uhc;

import net.samagames.gameapi.GameAPI;
import org.bukkit.ChatColor;

public class Messages
{
    public static final String PLUGIN_TAG = GameAPI.getCoherenceMachine("KTP").getGameTag();
    public static final String DEBUG_TAG = PLUGIN_TAG + ChatColor.GRAY + "[" + ChatColor.RED + "DEBUG" + ChatColor.GRAY + "] " + ChatColor.RESET;
    
    public static String notEnougthPlayers = PLUGIN_TAG + ChatColor.RED + "Il n'y a plus assez de joueurs pour commencer.";
    public static String startIn = PLUGIN_TAG + ChatColor.YELLOW + "Début du jeu dans " + ChatColor.AQUA + "${TIME}";
    public static String alreadyInArena = ChatColor.RED + "Vous êtes dèjà dans l'arène. Ceci est une erreur, merci de nous la signaler.";
    public static String arenaFull = ChatColor.RED + "L'arène est pleine.";
    public static String alreadyInGame = ChatColor.RED + "Vous êtes dèjà en jeu dans une autre arène.";
    public static String joinArena = PLUGIN_TAG + ChatColor.GREEN + "Vous avez rejoint le jeu.";
    public static String playerJoinedArena = PLUGIN_TAG + ChatColor.YELLOW + "${PSEUDO}" + ChatColor.YELLOW + " a rejoint la partie ! " + ChatColor.DARK_GRAY + "[" + ChatColor.RED + "${JOUEURS}" + ChatColor.DARK_GRAY + "/" + ChatColor.RED + "${JOUEURS_MAX}" + ChatColor.DARK_GRAY + "]";
    public static String gameStart = ChatColor.GOLD + "La partie commence !";
    public static String teamEliminated = PLUGIN_TAG + "L'équipe ${TEAM} est éliminée !";
    public static String playerQuitted = PLUGIN_TAG + "${PLAYER} s'est enfui du jeu :(";
    public static String win = PLUGIN_TAG + "L'équipe ${TEAM} gagne !";
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
    public static String adminOnly = PLUGIN_TAG + ChatColor.RED + "Cette équipe est réservée aux administrateurs !";
    public static String disconnected = PLUGIN_TAG + ChatColor.RED + "${PLAYER} s'est déconnecté et n'est pas revenu après 10 minutes :(";
    public static String teamLocked = PLUGIN_TAG + ChatColor.RED + "Votre équipe est maintenant fermée !";
    public static String teamUnlocked = PLUGIN_TAG + ChatColor.GREEN + "Votre équipe est maintenant ouverte !";
    public static String inviteSuccessful = PLUGIN_TAG + ChatColor.GREEN + "Le joueur a été invité !";
    public static String playersAvailable = PLUGIN_TAG + ChatColor.YELLOW + "Vous pouvez inviter les joueurs suivants :";
    public static String teamLockedJoin = PLUGIN_TAG + ChatColor.RED + "L'équipe choisie est fermée !";
    public static String teamLeaved = PLUGIN_TAG + ChatColor.GREEN + "Vous avez quitté l'équipe !";
    public static String dontTouchHer = PLUGIN_TAG + ChatColor.RED + ChatColor.BOLD + "NE TOUCHE PAS A MAITE !";
}