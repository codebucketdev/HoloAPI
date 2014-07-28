package de.codebucket.holoapi.nms;

import org.bukkit.Location;
import org.bukkit.World;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;

public class CraftWitherSkull implements CraftProtocol
{
	private static final int WITHER_SKULL = 66;
	
	private int entityId;
	private World world;
	private double defX;
	private double defY;
	private double defZ;
	
	public CraftWitherSkull(int entityId)
	{
		this(entityId, null, 0, 0, 0);
	}
	
	public CraftWitherSkull(int entityId, Location location)
	{
		this(entityId, location.getWorld(), location.getX(), location.getY(), location.getZ());
	}
	
	public CraftWitherSkull(int entityId, World world, double defX, double defY, double defZ)
	{
		this.entityId = entityId;;
		this.world = world;
		this.defX = defX;
		this.defY = defY;
		this.defZ = defZ;
	}

	@Override
	public int getEntityId() 
	{
		return entityId;
	}

	@Override
	public Location getLocation() 
	{
		return new Location(world, defX, defY, defZ);
	}
	
	@Override
	public World getWorld() 
	{
		return world;
	}

	@Override
	public AbstractPacket getSpawnPacket() 
	{
		WrapperPlayServerSpawnEntity skull = new WrapperPlayServerSpawnEntity();
		skull.setEntityID(getEntityId());
		skull.setType(WITHER_SKULL);
		skull.setX(getLocation().getX());
		skull.setY(getLocation().getY() +55);
		skull.setZ(getLocation().getZ());
		return skull;
	}
	
	@Override
	public AbstractPacket getMetadataPacket() 
	{
		return null;
	}

	@Override
	public AbstractPacket getPositionPacket()
	{
		WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
		packet.setEntityID(getEntityId());
		packet.setX(getLocation().getX());
		packet.setY(getLocation().getY() +55);
		packet.setZ(getLocation().getZ());
		packet.setPitch(getLocation().getPitch());
		packet.setYaw(getLocation().getYaw());
		return packet;
	}

	@Override
	public AbstractPacket getDestroyPacket()
	{
		WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
		packet.setEntities(new int[]{ getEntityId() });
		return packet;
	}
}