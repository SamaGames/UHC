package fr.blueslime.uhc;

import net.samagames.api.SamaGamesAPI;
import org.bukkit.ChatColor;

public enum Messages
{
    teamEliminated("L'équipe ${TEAM} est éliminée !", true),
    reducing("Réduction progressive de la bordure en cours en ${COORDS}. Courez !", true),
    reduced("La bordure a finie d'être réduite en ${COORDS} !", true),
    damageActivated(ChatColor.RED + "Les dégats sont maintenant activés !", true),
    pvpActivated(ChatColor.RED + "Les combats entre joueurs sont maintenant activés !", true),
    teamFull(ChatColor.RED + "L'équipe choisie est pleine :/", true),
    teamJoined(ChatColor.YELLOW + "Vous êtes entré dans l'équipe ${TEAM}" + ChatColor.YELLOW + " !", true),
    warningTeam(ChatColor.RED + "" + ChatColor.BOLD + "Les équipes non programmées sont interdites ! Préparez votre équipe à l'avance !", false),
    noTeam(ChatColor.RED + "Vous devez avoir une équipe pour pouvoir utiliser cette fonction !", true),
    teamNameChanged(ChatColor.YELLOW + "Le nom de l'équipe a été changé pour : ${NAME} " + ChatColor.YELLOW + "!", true),
    needVIP(ChatColor.RED + "Vous devez être VIP pour pouvoir utiliser cette fonction !", true),
    lineEmpty(ChatColor.RED + "Vous ne pouvez pas donner un nom d'équipe vide !", true),
    adminOnly(ChatColor.RED + "Cette équipe est réservée au développeur du jeu !", true),
    teamLocked(ChatColor.RED + "Votre équipe est maintenant fermée !", true),
    teamUnlocked(ChatColor.GREEN + "Votre équipe est maintenant ouverte !", true),
    inviteSuccessful(ChatColor.GREEN + "Le joueur a été invité !", true),
    playersAvailable(ChatColor.YELLOW + "Vous pouvez inviter les joueurs suivants :", true),
    teamLockedJoin(ChatColor.RED + "L'équipe choisie est fermée !", true),
    teamLeaved(ChatColor.GREEN + "Vous avez quitté l'équipe !", true),
    dontTouchHer(ChatColor.RED + "" + ChatColor.BOLD + "NE TOUCHE PAS A MAITE !", false),
    endOfGameAt(ChatColor.RED + "Fin de la partie forcée à la fin de l'épisode !", true),
    endOfGameIn(ChatColor.RED + "Fin de la partie forcée dans " + ChatColor.AQUA + "${TIME}", true),
    blockPlaceRefused(ChatColor.RED + "Vous ne pouvez pas poser ce bloc ici !", true),
    lavaPlaceRefused(ChatColor.RED + "Vous ne pouvez pas poser de la lave aussi près d'un portail !", true);

    private String message;
    private boolean tag;

    Messages(String message, boolean tag)
    {
        this.message = message;
        this.tag = tag;
    }

    @Override
    public String toString()
    {
        return (this.tag ? SamaGamesAPI.get().getGameManager().getCoherenceMachine().getGameTag() + " " : "") + this.message;
    }
}