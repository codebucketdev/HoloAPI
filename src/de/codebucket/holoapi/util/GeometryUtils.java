package de.codebucket.holoapi.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GeometryUtils 
{
    public static float generateRandomFloat(float min, float max)
    {
        float f = min + (GeneralUtils.random().nextFloat() * ((1 + max) - min));
        return GeneralUtils.random().nextBoolean() ? f : -f;
    }

    public static float generateRandomFloat() 
    {
        float f = GeneralUtils.random().nextFloat();
        return GeneralUtils.random().nextBoolean() ? f : -f;
    }

    public static List<Location> circle(Location origin, int radius, int height, boolean hollow, boolean sphere, boolean includeAir)
    {
        List<Location> blocks = new ArrayList<>();
        
        int cx = origin.getBlockX(), cy = origin.getBlockY(), cz = origin.getBlockZ();
        for (int x = cx - radius; x <= cx + radius; x++) 
        {
            for (int z = cz - radius; z <= cz + radius; z++) 
            {
                for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++) 
                {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) 
                    {
                        Location l = new Location(origin.getWorld(), x, y, z);
                        if (!includeAir && l.getBlock().getType() == Material.AIR) 
                        {
                            continue;
                        }
                        blocks.add(l);
                    }
                }
            }
        }
        return blocks;
    }

    public static boolean isInBorder(Location center, Location toCheck, int range)
    {
        int x = center.getBlockX(), z = center.getBlockZ();
        int x1 = toCheck.getBlockX(), z1 = toCheck.getBlockZ();
        return !(x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range));
    }

    @SuppressWarnings("unchecked")
	public static <T extends Entity> List<T> getNearbyEntities(Class<T> entityType, Location origin, int range) 
    {
        List<T> entities = new ArrayList<>();
        for (Entity entity : origin.getWorld().getEntities())
        {
            if (range <= 0 || isInBorder(origin, entity.getLocation(), range)) 
            {
                if (entityType.isAssignableFrom(entity.getClass()))
                {
                    entities.add((T) entity);
                }
            }
        }
        return entities;
    }

    public static List<Entity> getNearbyEntities(Location origin, int range)
    {
        return getNearbyEntities(Entity.class, origin, range);
    }

    public static List<Player> getNearbyPlayers(Location origin, int range) 
    {
        return getNearbyEntities(Player.class, origin, range);
    }
}