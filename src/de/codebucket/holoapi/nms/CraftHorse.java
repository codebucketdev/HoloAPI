package de.codebucket.holoapi.nms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class CraftHorse implements CraftProtocol
{
	private int entityId;
	private String name;
	private World world;
	private double defX;
	private double defY;
	private double defZ;
	
	public CraftHorse(int entityId)
	{
		this(entityId, null, null, 0, 0, 0);
	}
	
	public CraftHorse(int entityId, String name, Location location)
	{
		this(entityId, name, location.getWorld(), location.getX(), location.getY(), location.getZ());
	}
	
	public CraftHorse(int entityId, String name, World world, double defX, double defY, double defZ)
	{
		this.entityId = entityId;
		this.name = name;
		this.world = world;
		this.defX = defX;
		this.defY = defY;
		this.defZ = defZ;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public int getEntityId()
	{
		return entityId;
	}
	
	@Override
	public World getWorld()
	{
		return world;
	}

	@Override
	public Location getLocation() 
	{
		return new Location(world, defX, defY, defZ);
	}
	
	@Override
	public AbstractPacket getSpawnPacket()
	{
		WrapperPlayServerSpawnEntityLiving horse = new WrapperPlayServerSpawnEntityLiving();
		horse.setEntityID(getEntityId());
		horse.setType(EntityType.HORSE);
		horse.setX(getLocation().getX());
		horse.setY(getLocation().getY() +55);
		horse.setZ(getLocation().getZ());
		 
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(10, getName());
		watcher.setObject(11, (byte) 1);
		watcher.setObject(12, -1700000);
		horse.setMetadata(watcher);
		return horse;
	}
	
	public void sendSpawnPacket(Player player, Location location, String name)
	{
		WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
		packet.setEntityID(getEntityId());
		packet.setType(EntityType.HORSE);
		packet.setX(location.getX());
		packet.setY(location.getY() +55);
		packet.setZ(location.getZ());
		 
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(10, name);
		watcher.setObject(11, (byte) 1);
		watcher.setObject(12, -1700000);
		packet.setMetadata(watcher);
		packet.sendPacket(player);
	}
	
	@Override
	public AbstractPacket getMetadataPacket() 
	{
		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(10, getName());
		watcher.setObject(11, (byte) 1);
		watcher.setObject(12, -1700000);
		
		meta.setEntityId(getEntityId());
		meta.setEntityMetadata(watcher.getWatchableObjects());
		return meta;
	}
	
	public void sendMetadataPacket(Player player, String name, int age)
	{
		WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(10, name);
		watcher.setObject(11, (byte) 1);
		watcher.setObject(12, age);
		
		packet.setEntityId(getEntityId());
		packet.setEntityMetadata(watcher.getWatchableObjects());
		packet.sendPacket(player);
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
	
	public void sendPositionPacket(Player player, Location location)
	{
		WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
		packet.setEntityID(getEntityId());
		packet.setX(location.getX());
		packet.setY(location.getY() +55);
		packet.setZ(location.getZ());
		packet.setPitch(location.getPitch());
		packet.setYaw(location.getYaw());
		packet.sendPacket(player);
	}

	@Override
	public AbstractPacket getDestroyPacket()
	{
		WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
		packet.setEntities(new int[]{ getEntityId() });
		return packet;
	}
}
