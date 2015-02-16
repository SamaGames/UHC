package fr.blueslime.uhc.utils;

import net.zyuiop.MasterBundle.MasterBundle;
import redis.clients.jedis.Jedis;

public class ModMessage
{
    public static void broadcastModMessage(String from, String message)
    {
        String def = "mod#####" + from + "#####" + message;

        Jedis j = MasterBundle.getRedisBungee();
        j.publish("redisbungee-allservers", def);
        j.close();
    }
}
