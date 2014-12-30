package fr.blueslime.uhc.events.common;

import fr.blueslime.uhc.utils.OtherUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UHCPotionSplashEvent implements Listener
{
    @EventHandler
    public void event(PotionSplashEvent event)
    {
        ThrownPotion potion = event.getPotion();

        if (potion.getShooter() instanceof Witch)
        {
            List<PotionEffectType> popos = new ArrayList<>();
            popos.add(PotionEffectType.SLOW_DIGGING);
            popos.add(PotionEffectType.CONFUSION);
            popos.add(PotionEffectType.NIGHT_VISION);
            popos.add(PotionEffectType.HUNGER);
            popos.add(PotionEffectType.BLINDNESS);
            
            Collection<LivingEntity> list = event.getAffectedEntities();
        
            event.setCancelled(true);
            
            for (LivingEntity ent : list)
            {
                ent.addPotionEffect(new PotionEffect(popos.get(OtherUtils.randInt(0, popos.size() - 1)), 20 * 15, 1));
            }
        }
    }
}
