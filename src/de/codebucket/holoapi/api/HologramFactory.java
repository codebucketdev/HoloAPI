package de.codebucket.holoapi.api;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import de.codebucket.holoapi.HoloAPI;
import de.codebucket.holoapi.util.ImageChar;
import de.codebucket.holoapi.util.ImageMessage;

public class HologramFactory 
{
	private Plugin owningPlugin;
	
    protected String worldName;
    protected double locX;
    protected double locY;
    protected double locZ;
    
    protected int tagId;
    protected boolean withTagId;
    protected boolean simple;
    
    private boolean prepared = false;
    private List<String> tags = new ArrayList<>();
    
    public HologramFactory(Plugin owningPlugin) 
    {
        if(owningPlugin == null) 
        {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.owningPlugin = owningPlugin;
    }
    
    public boolean isEmpty() 
    {
        return this.tags.isEmpty();
    }
    
    public HologramFactory clearContent() 
    {
        this.tags.clear();
        return this;
    }
    
    public HologramFactory withSimplicity(boolean simple)
    {
        this.simple = simple;
        return this;
    }
    
    public HologramFactory withText(String... text) 
    {
        Collections.addAll(this.tags, text);
        return this;
    }
    
    public HologramFactory withImage(BufferedImage image) 
    {
    	ImageMessage message = new ImageMessage(image, image.getHeight(), ImageChar.BLOCK.getChar());
        Collections.addAll(this.tags, message.getLines());
        return this;
    }
    
    public HologramFactory withLocation(Location location)
    {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }
    
    public HologramFactory withLocation(Vector vectorLocation, String worldName) 
    {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }
    
    public HologramFactory withCoords(double x, double y, double z)
    {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.prepared = true;
        return this;
    }

    public HologramFactory withWorld(String worldName)
    {
        this.worldName = worldName;
        return this;
    }

    public boolean canBuild()
    {
    	return !this.isEmpty();
    }

    protected Hologram prepareHologram()
    {
    	String[] content = this.tags.toArray(new String[this.tags.size()]);
        int startId = IdentifierGenerator.nextId(content.length);
        if(withTagId == true)
        {
        	startId = tagId;
        }
        
        return new Hologram(worldName, locX, locY, locZ, startId, content);
    }
    
    public Hologram build() 
    {
        if(!this.canBuild() || !this.prepared) 
        {
            throw new IllegalStateException("Hologram is not prepared correctly!");
        }
        
        Hologram hologram = prepareHologram();
        if(Bukkit.getWorld(this.worldName) == null) 
        {
        	throw new RuntimeException("Could not find valid world (" + this.worldName + ") for Hologram of ID " + hologram.getStartId() + "!");
        }
        hologram.updateDisplay();
        hologram.showNearby();
        
        if(this.simple)
        {
        	HoloAPI.getHandler().registerEvents(new HoloListener(owningPlugin, hologram, HoloAPI.getHandler()), owningPlugin);
        }
        HoloAPI.getManager().track(hologram, this.owningPlugin);
        return hologram;
    }

    protected HologramFactory withFirstTagId(int tagId) 
    {
        this.tagId = tagId;
        this.withTagId = true;
        return this;
    }
}
