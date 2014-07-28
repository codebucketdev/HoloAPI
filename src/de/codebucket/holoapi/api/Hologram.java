package de.codebucket.holoapi.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.comphenix.packetwrapper.*;

import de.codebucket.holoapi.HoloAPI;
import de.codebucket.holoapi.nms.AttachEntity;
import de.codebucket.holoapi.nms.CraftHorse;
import de.codebucket.holoapi.nms.CraftWitherSkull;
import de.codebucket.holoapi.util.GeometryUtils;

public class Hologram implements Listener
{
	public static int TAG_ENTITY_MULTIPLIER = 2;
	
	private String worldName;
    private double defX;
    private double defY;
    private double defZ;
    
    private boolean simple;
    private final int startId;
    private String[] tags;
    
    private Map<UUID, Vector> playerViews;
    
    protected Hologram(String worldName, double defX, double defY, double defZ, int startId, String[] lines)
    {
    	this.worldName = worldName;
		this.defX = defX;
		this.defY = defY;
		this.defZ = defZ;
		
		String[] tags = new String[lines.length];
		for(int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			if(line.length() > 32)
			{
				line = line.substring(0, 32);
			}
			tags[i] = line;
		}
		
		this.playerViews = new HashMap<>();
		this.startId = startId;
		this.tags = tags;
    }
	
	public int getStartId()
	{
		return this.startId;
	}
	
	public int getTagCount()
	{
        return this.tags.length;
    }
	
    public double getDefaultX() 
    {
        return this.defX;
    }
    
    public double getDefaultY() 
    {
        return this.defY;
    }
    
    public double getDefaultZ() 
    {
        return this.defZ;
    }
    
    public String getWorldName() 
    {
        return this.worldName;
    }
    
    public World getWorld()
    {
    	return Bukkit.getWorld(getWorldName());
    }
    
    public Location getDefaultLocation()
    {
    	return new Location(getWorld(), getDefaultX(), getDefaultY(), getDefaultZ());
    }
    
    public boolean isSimple()
	{
		return this.simple;
	}
	
	protected void setSimple(boolean simple)
	{
		this.simple = simple;
	}
    
    public HashMap<UUID, Vector> getPlayerViews() 
    {
        HashMap<UUID, Vector> map = new HashMap<>();
        map.putAll(this.playerViews);
        return map;
    }
    
    public boolean canSeenBy(Player player) 
    {
        return getPlayerViews().containsKey(player.getUniqueId());
    }

    public Vector getPlayerView(Player player)
    {
        return getPlayerViews().get(player.getUniqueId());
    }
    
    public String[] getLines()
    {
    	return this.tags;
    }
    
    public void changeWorld(String worldName) 
    {
        this.clearAllPlayerViews();
        
        this.worldName = worldName;
        for(Player pl : getDefaultLocation().getWorld().getPlayers())
        {
        	this.show(pl);
        }
    }
    
    public void clearAllPlayerViews() 
    {
        Iterator<UUID> i = playerViews.keySet().iterator();
        while(i.hasNext()) 
        {
            Player pl = Bukkit.getPlayer(i.next());
            if(pl != null) 
            {
                this.clearTags(pl, this.getAllEntityIds());
            }
            i.remove();
        }
    }

    public Vector getLocationFor(Player player) 
    {
        return this.playerViews.get(player.getUniqueId());
    }
    
    public void remove()
    {
    	if(!HoloAPI.getManager().isTracked(this))
    	{
    		this.clearAllPlayerViews();
    	}
    	HoloAPI.getManager().stopTracking(this);
    	this.playerViews = new HashMap<>();
    	this.tags = new String[0];
    }

    public void updateLine(int index, String content) 
    {
        if(index >= this.tags.length)
        {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        }
        
        this.tags[index] = content;
        for(UUID ident : this.playerViews.keySet()) 
        {
            Player pl = Bukkit.getPlayer(ident);
            if(pl != null) 
            {
                this.updateNametag(pl, this.tags[index], index);
            }
        }
    }
    
    public void updateLine(int index, String content, UUID uuid) 
    {
        this.updateLine(index, content, Bukkit.getPlayer(uuid));
    }

    public void updateLine(int index, String content, Player observer) 
    {
        if(index >= this.tags.length) 
        {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        }
        
        if(observer != null)
        {
            this.updateNametag(observer, content, index);
        }
    }
    
    public void updateDisplay(UUID uuid) 
    {
        this.updateDisplay(Bukkit.getPlayer(uuid));
    }
    
    public void updateDisplay(Player observer) 
    {
        for(int index = 0; index < this.tags.length; index++)
        {
            this.updateNametag(observer, this.tags[index], index);
        }
    }
    
    public void updateDisplay()
    {
        for(UUID ident : this.getPlayerViews().keySet())
        {
            Player pl = Bukkit.getPlayer(ident);
            if(pl != null) 
            {
                this.updateDisplay(pl);
            }
        }
    }
    
    public void updateLines(String... content) 
    {
        if(content.length <= 0) 
        {
            throw new IllegalArgumentException("New hologram content cannot be empty!");
        }

        // Make sure it's not too long
        String[] cont = content;
        if(cont.length > this.tags.length) 
        {
            cont = new String[this.tags.length];
            System.arraycopy(content, 0, cont, 0, this.tags.length);
        }
        
        for(UUID ident : this.playerViews.keySet()) 
        {
            Player pl = Bukkit.getPlayer(ident);
            if(pl != null)
            {
            	this.tags = cont;
                for(int index = 0; index < cont.length; index++) 
                {
                    this.updateNametag(pl, this.tags[index], index);
                }
            }
        }
    }
    
    public void updateLines(UUID uuid, String... content) 
    {
    	this.updateLines(Bukkit.getPlayer(uuid), content);
    }

    public void updateLines(Player observer, String... content) 
    {
        if(content.length <= 0) 
        {
            throw new IllegalArgumentException("New hologram content cannot be empty!");
        }
        
        // Make sure it's not too long
        String[] cont = content;
        if(cont.length > this.tags.length) 
        {
            cont = new String[this.tags.length];
            System.arraycopy(content, 0, cont, 0, 30);
        }

        if(observer != null) 
        {
            for(int index = 0; index < cont.length; index++)
            {
                this.updateNametag(observer, cont[index], index);
            }
        }
    }

    public int[] getAllEntityIds() 
    {
        ArrayList<Integer> entityIdList = new ArrayList<>();
        for(int index = 0; index < this.getTagCount(); index++) 
        {
            for(int i = 0; i < TAG_ENTITY_MULTIPLIER; i++) 
            {
                entityIdList.add(this.getHorseIndex(index) + i);
            }
        }

        int[] ids = new int[entityIdList.size()];
        for(int i = 0; i < ids.length; i++) 
        {
            ids[i] = entityIdList.get(i);
        }

        return ids;
    }
    
    public void show(Player observer) 
    {
        this.show(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ());
    }
    
    public void show(Player observer, Location location) 
    {
        this.show(observer, location.getX(), location.getY(), location.getZ());
    }
    
    public void show(Player observer, double x, double y, double z)
    {
        for(int index = 0; index < this.getTagCount(); index++) 
        {
            this.generateTag(observer, this.tags[index], index, -index * 0.25D, x, y, z);
        }
        this.playerViews.put(observer.getUniqueId(), new Vector(x, y, z));
    }
    
    public void showNearby(Location origin, int radius) 
    {
        for(Player player : GeometryUtils.getNearbyPlayers(origin, radius)) 
        {
            this.show(player);
        }
    }
    
    public void showNearby(int radius) 
    {
        this.showNearby(getDefaultLocation(), radius);
    }
    
    public void showNearby() 
    {
        this.showNearby(getDefaultLocation(), -1);
    }
    
    public void showNearby(double x, double y, double z, int radius)
    {
        this.showNearby(new Location(Bukkit.getWorld(this.getWorldName()), x, y, z), radius);
    }
    
    public void move(Location to)
    {
        if(!this.worldName.equals(to.getWorld().getName()))
        {
            this.changeWorld(to.getWorld().getName());
        }
        this.move(to.toVector());
    }
    
    public void move(Vector to) 
    {
        this.defX = to.getX();
        this.defY = to.getY();
        this.defZ = to.getZ();
        
        for(UUID ident : this.getPlayerViews().keySet()) 
        {
            Player pl = Bukkit.getPlayer(ident);
            if(pl != null)
            {
                this.move(pl, to);
            }
        }
    }
    
    public void move(Player observer, Vector to) 
    {
        Vector loc = to.clone();
        for(int index = 0; index < this.getTagCount(); index++) 
        {
            this.moveTag(observer, index, loc);
            double locY = loc.getY();
            loc.setY(locY - 0.25D);
        }
        this.playerViews.put(observer.getUniqueId(), to);
    }
    
    public void hide()
    {
    	this.hide(getWorld().getPlayers());
    }
    
    public void hide(List<Player> observers) 
    {
    	for(Player observer : observers)
    	{
    		this.hide(observer);
    	}
    }
    
    public void hide(Player observer) 
    {
        clearTags(observer, this.getAllEntityIds());
        UUID uniqueIdent = observer.getUniqueId();
        this.playerViews.remove(uniqueIdent);
    }
    
    protected void clearTags(Player observer, int... entityIds)
    {
        if(entityIds.length > 0) 
        {
        	WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
    		packet.setEntities(entityIds);
    		packet.sendPacket(observer);
        }
    }

    protected void clearTag(Player observer, int index)
    {
    	CraftHorse crafthorse = new CraftHorse(getHorseIndex(index));
        AbstractPacket horse = crafthorse.getDestroyPacket();
        horse.sendPacket(observer);
        
        CraftWitherSkull craftskull = new CraftWitherSkull(getSkullIndex(index));
        AbstractPacket skull = craftskull.getDestroyPacket();
        skull.sendPacket(observer);
        
        AttachEntity attach = new AttachEntity(getDefaultLocation(), craftskull.getEntityId(), crafthorse.getEntityId());
        AbstractPacket packet = attach.getDestroyPacket();
        packet.sendPacket(observer);
    }

    protected void moveTag(Player observer, int index, Vector toVector)
    {
    	CraftHorse crafthorse = new CraftHorse(getHorseIndex(index), null, getWorld(), toVector.getX(), toVector.getY(), toVector.getZ());
        AbstractPacket horse = crafthorse.getPositionPacket();
        horse.sendPacket(observer);
        
        CraftWitherSkull craftskull = new CraftWitherSkull(getSkullIndex(index), getWorld(), toVector.getX(), toVector.getY(), toVector.getZ());
        AbstractPacket skull = craftskull.getPositionPacket();
        skull.sendPacket(observer);
        
        AttachEntity attach = new AttachEntity(new Location(getWorld(), toVector.getX(), toVector.getY(), toVector.getZ()), craftskull.getEntityId(), crafthorse.getEntityId());
        AbstractPacket packet = attach.getPositionPacket();
        packet.sendPacket(observer);
    }
    
    protected void generateTag(Player observer, String message, int index, double diffY, double locX, double locY, double locZ) 
    {
    	CraftHorse crafthorse = new CraftHorse(getHorseIndex(index), message, getWorld(), locX, (locY + diffY), locZ);
        AbstractPacket horse = crafthorse.getSpawnPacket();
        horse.sendPacket(observer);
        
        CraftWitherSkull craftskull = new CraftWitherSkull(getSkullIndex(index), getWorld(), locX, (locY + diffY), locZ);
        AbstractPacket skull = craftskull.getSpawnPacket();
        skull.sendPacket(observer);
        
        AttachEntity attach = new AttachEntity(new Location(getWorld(), locX, (locY + diffY), locZ), craftskull.getEntityId(), crafthorse.getEntityId());
        AbstractPacket packet = attach.getSpawnPacket();
        packet.sendPacket(observer);
    }

    protected void updateNametag(Player observer, String message, int index) 
    {
    	CraftHorse horse = new CraftHorse(getHorseIndex(index), message, getPlayerView(observer).toLocation(getWorld()));
        AbstractPacket packet = horse.getMetadataPacket();
        packet.sendPacket(observer);
    }
	
	protected int getHorseIndex(int index) 
	{
        return startId + (index * TAG_ENTITY_MULTIPLIER);
    }

    protected int getSkullIndex(int index) 
    {
        return this.getHorseIndex(index) + 1;
    }

    protected int getTouchSkullIndex(int index) 
    {
        return this.getSkullIndex(index) + 2;
    }
}
