package de.codebucket.holoapi.nms;

import org.bukkit.Location;
import org.bukkit.World;

import com.comphenix.packetwrapper.AbstractPacket;

public interface CraftProtocol
{
	public int getEntityId();
	
	public World getWorld();
	
	public Location getLocation();
	
	public AbstractPacket getSpawnPacket();
	
	public AbstractPacket getMetadataPacket();
	
	public AbstractPacket getPositionPacket();
	
	public AbstractPacket getDestroyPacket();
}
