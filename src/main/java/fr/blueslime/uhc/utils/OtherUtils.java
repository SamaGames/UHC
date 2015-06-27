package fr.blueslime.uhc.utils;

import java.util.Random;

public class OtherUtils
{
    public static int randInt(int min, int max)
    {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
