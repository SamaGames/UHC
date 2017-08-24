package net.samagames.uhc;

import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import net.samagames.survivalapi.SurvivalAPI;
import net.samagames.survivalapi.game.SurvivalGame;
import net.samagames.survivalapi.game.SurvivalGameLoop;
import net.samagames.survivalapi.game.types.SurvivalSoloGame;
import net.samagames.survivalapi.game.types.SurvivalTeamGame;
import net.samagames.survivalapi.modules.craft.DisableLevelTwoPotionModule;
import net.samagames.survivalapi.modules.craft.DisableNotchAppleModule;
import net.samagames.survivalapi.modules.craft.DisableSpeckedMelonModule;
import net.samagames.survivalapi.modules.craft.OneShieldModule;
import net.samagames.survivalapi.modules.gameplay.LoveMachineModule;
import net.samagames.survivalapi.modules.gameplay.PersonalBlocksModule;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * This file is part of UHC.
 *
 * UHC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UHC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UHC.  If not, see <http://www.gnu.org/licenses/>.
 */
public class UHC extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        SurvivalGame game;

        int nb = SamaGamesAPI.get().getGameManager().getGameProperties().getOption("playersPerTeam", new JsonPrimitive(1)).getAsInt();

        SurvivalAPI.get().loadModule(DisableLevelTwoPotionModule.class, null);
        SurvivalAPI.get().loadModule(DisableNotchAppleModule.class, null);
        SurvivalAPI.get().loadModule(DisableSpeckedMelonModule.class, null);
        SurvivalAPI.get().loadModule(PersonalBlocksModule.class, null);
        SurvivalAPI.get().loadModule(LoveMachineModule.class, null);
        SurvivalAPI.get().loadModule(OneShieldModule.class, null);

        if (nb > 1)
            game = new SurvivalTeamGame<>(this, "uhc", "UHC", "La survie en Ultra Hard Core", null, SurvivalGameLoop.class, nb);
        else
            game = new SurvivalSoloGame<>(this, "uhc", "UHC", "La survie en Ultra Hard Core", null, SurvivalGameLoop.class);

        SamaGamesAPI.get().getStatsManager().setStatsToLoad(GamesNames.UHCORIGINAL, true);
        SamaGamesAPI.get().getShopsManager().setShopToLoad(GamesNames.UHCORIGINAL, true);
        SamaGamesAPI.get().getGameManager().setGameStatisticsHelper(new UHCStatisticsHelper());
        SamaGamesAPI.get().getGameManager().registerGame(game);
    }
}