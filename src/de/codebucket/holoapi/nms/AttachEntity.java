package de.codebucket.holoapi.nms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerAttachEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;

public class AttachEntity implements CraftProtocol
{
	private Location location;
	private int vehicleId;
	private int entityId;
	
	public AttachEntity(Location location, int vehicleId, int entityId)
	{
		this.location = location;
		this.vehicleId = vehicleId;
		this.entityId = entityId;
	}
	
	public int getVehicleId()
	{
		return vehicleId;
	}
	
	@Override
	public int getEntityId() 
	{
		return entityId;
	}

	@Override
	public Location getLocation() 
	{
		return location;
	}
	
	@Override
	public World getWorld() 
	{
		return location.getWorld();
	}

	@Override
	public AbstractPacket getSpawnPacket() 
	{
		WrapperPlayServerAttachEntity attach = new WrapperPlayServerAttachEntity();
		attach.setEntityId(getEntityId());
		attach.setVehicleId(getVehicleId());
		return attach;
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
		packet.setEntityID(getVehicleId());
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
		packet.setEntityID(getVehicleId());
		packet.setX(location.getX());
		packet.setY(location.getY() +55);
		packet.setZ(location.getZ());
		packet.setPitch(getLocation().getPitch());
		packet.setYaw(getLocation().getYaw());
		packet.sendPacket(player);
	}

	@Override
	public AbstractPacket getDestroyPacket()
	{
		WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
		packet.setEntities(new int[]{ getVehicleId() });
		return packet;
	}
	
}
