package de.codebucket.holoapi.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GeneralUtils
{	
    private static SecureRandom RANDOM;

    public static SecureRandom random()
    {
        if (RANDOM == null) 
        {
            RANDOM = new SecureRandom();
        }
        return RANDOM;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean isEnumType(Class<? extends Enum> clazz, String toTest) 
    {
        try
        {
            Enum.valueOf(clazz, toTest.toUpperCase());
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
    }

    /**
     * Tests if the given String is an Integer
     *
     * @param toTest the String to be checked
     * @return true if Integer
     */
    public static boolean isInt(String toTest)
    {
        try 
        {
            Integer.parseInt(toTest);
        } 
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
    }

    /**
     * Tests if the given String is an Double
     *
     * @param toTest the String to be checked
     * @return true if Double
     */
    public static boolean isDouble(String toTest) 
    {
        try 
        {
            Double.parseDouble(toTest);
        } 
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
    }

    public static Location readLocation(int startIndex, String... args) 
    {
        World world = Bukkit.getWorld(args[startIndex]);
        if (world == null) 
        {
            throw new IllegalArgumentException("World does not exist!");
        }
        
        double[] coords = new double[3];
        int index = 0;
        for (int i = startIndex + 1; i < startIndex + 4; i++)
        {
            if (!isDouble(args[i])) 
            {
                return null;
            }
            coords[index++] = Double.parseDouble(args[i]);
        }
        return new Location(world, coords[0], coords[1], coords[2]);
    }

    public static <K, V> K getKeyAtValue(Map<K, V> map, V value)
    {
        if (map instanceof BiMap)
        {
            return ((BiMap<K, V>) map).inverse().get(value);
        }
        
        for (Map.Entry<K, V> entry : map.entrySet())
        {
            if (entry.getValue().equals(value))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    public static <V, K> Map<V, K> invertMap(Map<K, V> map) 
    {
        Map<V, K> inverted = new HashMap<V, K>();
        for (Map.Entry<K, V> entry : map.entrySet()) 
        {
            inverted.put(entry.getValue(), entry.getKey());
        }
        return inverted;
    }

    /**
     * Attempts to convert a string into an integer value using Regex
     *
     * @param string the String to be checked
     * @throws java.lang.NumberFormatException
     */
    public static int toInteger(String string) throws NumberFormatException 
    {
        try 
        {
            return Integer.parseInt(string.replaceAll("[^\\d]", ""));
        } 
        catch (NumberFormatException e) 
        {
            throw new NumberFormatException(string + " isn't a number!");
        }
    }

    /**
     * Attempts to convert a string into an double value using Regex
     *
     * @param string the String to be checked
     * @return Double.MIN_VALUE if unable to convert
     * @throws java.lang.NumberFormatException
     */
    public static double toDouble(String string) 
    {
        try 
        {
            return Double.parseDouble(string.replaceAll(".*?([\\d.]+).*", "$1"));
        } 
        catch (NumberFormatException e) 
        {
            throw new NumberFormatException(string + " isn't a number!");
        }
    }

    public static <T> List<T> merge(T[] first, T[] second) 
    {
        ArrayList<T> merged = new ArrayList<>();
        Collections.addAll(merged, first);
        Collections.addAll(merged, second);
        return merged;
    }
}
