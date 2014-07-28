package de.codebucket.holoapi.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import de.codebucket.holoapi.HoloAPI;


public class HoloManager
{
    private HashMap<Hologram, Plugin> holograms = new HashMap<>();

    public HoloManager()
    {
        new BukkitRunnable()
        {
            @Override
            public void run() 
            {
                if(!getAllHolograms().isEmpty())
                {
                    for(Hologram hologram : getAllHolograms().keySet())
                    {
                        hologram.updateDisplay();
                    }
                }
            }
        }.runTaskTimer(HoloAPI.getCore(), 0L, 20 * 60);
    }

    public Map<Hologram, Plugin> getAllHolograms() 
    {
        return Collections.unmodifiableMap(this.holograms);
    }

    public void clearAll() 
    {
        Iterator<Hologram> i = holograms.keySet().iterator();
        while(i.hasNext()) 
        {
            Hologram h = i.next();
            h.clearAllPlayerViews();
            i.remove();
        }
    }
    
    public List<Hologram> getHologramsFor(Plugin owningPlugin) 
    {
        ArrayList<Hologram> list = new ArrayList<>();
        for(Map.Entry<Hologram, Plugin> entry : this.holograms.entrySet()) 
        {
            if(entry.getValue().equals(owningPlugin)) 
            {
                list.add(entry.getKey());
            }
        }
        return Collections.unmodifiableList(list);
    }
    
    public Hologram getHologram(int hologramId)
    {
        for(Hologram hologram : this.holograms.keySet()) 
        {
            if(hologram.getStartId() == hologramId) 
            {
                return hologram;
            }
        }
        return null;
    }
    
    public boolean isTracked(Hologram hologram)
    {
    	for(Hologram holo : this.holograms.keySet())
    	{
    		if(holo.equals(hologram))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isTracked(HoloListener listener)
    {
    	for(Hologram holo : getHologramsFor(listener.getPlugin())) 
        {
    		if(holo.equals(listener.getHologram()))
    		{
    			return true;
    		}
        }
    	return false;
    }
    
    public boolean isTracked(int hologramId)
    {
    	for(Hologram holo : this.holograms.keySet()) 
        {
            if(holo.getStartId() == hologramId) 
            {
                return true;
            }
        }
    	return false;
    }
    
    public void track(Hologram hologram, Plugin owningPlugin) 
    {
        this.holograms.put(hologram, owningPlugin);
    }
    
    public void remove(Hologram hologram)
    {
        stopTracking(hologram);
    }
    
    public void remove(HoloListener listener)
    {
        stopTracking(listener);
    }
    
    public void remove(int hologramId) 
    {
        stopTracking(hologramId);
    }
    
    public void stopTracking(HoloListener listener) 
    {
    	listener.getHologram().clearAllPlayerViews();
        if(this.holograms.containsKey(listener.getHologram()))
        {
        	this.holograms.remove(listener.getHologram());
        }
    }
    
    public void stopTracking(Hologram hologram) 
    {
        hologram.clearAllPlayerViews();
        if(this.holograms.containsKey(hologram))
        {
        	this.holograms.remove(hologram);
        }
    }
    
    public void stopTracking(int hologramId)
    {
        Hologram hologram = this.getHologram(hologramId);
        if(hologram != null) 
        {
            this.stopTracking(hologram);
        }
    }
    
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, List<String> lines) 
    {
        return this.createSimpleHologram(location, secondsUntilRemoved, false, lines.toArray(new String[lines.size()]));
    }
    
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, boolean rise, List<String> lines) 
    {
        return this.createSimpleHologram(location, secondsUntilRemoved, rise, lines.toArray(new String[lines.size()]));
    }
    
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, String... lines)
    {
        return this.createSimpleHologram(location, secondsUntilRemoved, false, lines);
    }
    
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, boolean rise, String... lines) 
    {
        int simpleId = IdentifierGenerator.nextSimpleId(lines.length);
        final Hologram hologram = new HologramFactory(HoloAPI.getCore()).withFirstTagId(simpleId).withText(lines).withLocation(location).withSimplicity(true).build();
        for(Player pl : hologram.getDefaultLocation().getWorld().getPlayers()) 
        {
        	hologram.show(pl);
        }
        
        BukkitTask task = null;
        if(rise == true)
        {
            task = HoloAPI.getCore().getServer().getScheduler().runTaskTimer(HoloAPI.getCore(), new Runnable()
            {
                @Override
                public void run()
                {
                    Location l = hologram.getDefaultLocation();
                    l.add(0.0D, 0.02D, 0.0D);
                    hologram.move(l.toVector());
                }
            }, 1L, 1L);
        }
        
        new HologramRemoveTask(hologram, task).runTaskLater(HoloAPI.getCore(), secondsUntilRemoved * 20);
        return hologram;
    }
    
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, Vector velocity, List<String> lines)
    {
        return this.createSimpleHologram(location, secondsUntilRemoved, velocity, lines.toArray(new String[lines.size()]));
    }
    
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, final Vector velocity, String... lines) 
    {
        int simpleId = IdentifierGenerator.nextSimpleId(lines.length);
        final Hologram hologram = new HologramFactory(HoloAPI.getCore()).withFirstTagId(simpleId).withText(lines).withLocation(location).withSimplicity(true).build();
        for(Player pl : hologram.getDefaultLocation().getWorld().getPlayers()) 
        {
        	hologram.show(pl);
        }

        BukkitTask task = HoloAPI.getCore().getServer().getScheduler().runTaskTimer(HoloAPI.getCore(), new Runnable()
        {
            @Override
            public void run() 
            {
                Location l = hologram.getDefaultLocation();
                l.add(velocity);
                hologram.move(l.toVector());
            }
        }, 1L, 1L);
        
        new HologramRemoveTask(hologram, task).runTaskLater(HoloAPI.getCore(), secondsUntilRemoved * 20);
        return hologram;
    }

    class HologramRemoveTask extends BukkitRunnable
    {
        BukkitTask task = null;
        private Hologram hologram;

        HologramRemoveTask(Hologram hologram, BukkitTask task) 
        {
            this.hologram = hologram;
            this.task = task;
        }

        @Override
        public void run()
        {
            if(this.task != null) 
            {
                task.cancel();
            }
            hologram.remove();
        }
    }
}