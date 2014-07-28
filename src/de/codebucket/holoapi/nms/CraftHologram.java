package de.codebucket.holoapi.nms;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Deprecated
public class CraftHologram
{	
	private World world;
	private double defX;
	private double defY;
	private double defZ;
	
	private int id;
	private String text;
	private CraftHorse horse;
	private CraftWitherSkull skull;
	private AttachEntity attach;
	
	public CraftHologram(int id, Location location, String text)
	{
		this(id, location.getWorld(), location.getX(), location.getY(), location.getZ(), text);
	}
	
	public CraftHologram(int id, World world, double defX, double defY, double defZ, String text)
	{
		this.id = id;
		this.world = world;
		this.defX = defX;
		this.defY = defY;
		this.defZ = defZ;
		this.text = text;
		
		this.generate();
	}
	
	private void generate()
	{
		this.horse = new CraftHorse(getId() +1, getText(), getLocation());
		this.skull = new CraftWitherSkull(getId() +2, getLocation());
		this.attach = new AttachEntity(getLocation(), skull.getEntityId(), horse.getEntityId());
	}
	
	public int getId()
	{
		return id;
	}
	
	@Deprecated
	public void setId(int id)
	{
		this.id = id;
		this.generate();
	}
	
	public Location getLocation()
	{
		return new Location(world, defX, defY, defZ);
	}
	
	public void setLocation(Location location)
	{
		this.world = location.getWorld();
		this.defX = location.getX();
		this.defY = location.getY();
		this.defZ = location.getZ();	
		this.generate();
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
		this.generate();
	}
	
	public void teleport(Location location, Player... players)
	{
		setLocation(location);
		this.move(players);
	}
	
	protected void move(Player... players)
	{
		for(Player player : players)
		{
			for(CraftProtocol packet : getPackets())
			{
				packet.getPositionPacket().sendPacket(player);
			}
		}
	}
	
	public void show()
	{
		show(world.getPlayers());
	}
	
	public void show(List<Player> players)
	{
		for(Player player : players)
		{
			show(player);
		}
	}
	
	public void show(Player player)
	{
		for(CraftProtocol packet : getPackets())
		{
			packet.getSpawnPacket().sendPacket(player);
		}
	}
	
	public void hide()
	{
		hide(world.getPlayers());
	}
	
	public void hide(List<Player> players)
	{
		for(Player player : players)
		{
			hide(player);
		}
	}
	
	public void hide(Player player)
	{		
		for(CraftProtocol packet : getPackets())
		{
			packet.getDestroyPacket().sendPacket(player);
		}
	}
	
	public void updateDisplay(List<Player> players)
	{
		for(Player player : players)
		{
			updateDisplay(player);
		}
	}
	
	public void updateDisplay(Player player)
	{
		for(CraftProtocol packet : getPackets())
		{
			if(packet.getMetadataPacket() != null)
			{
				packet.getMetadataPacket().sendPacket(player);
			}
			packet.getPositionPacket().sendPacket(player);
		}
	}
	
	protected void sendUpdate(Player player, Location location, String text)
	{
		horse.sendMetadataPacket(player, text, -1700000);
		horse.sendPositionPacket(player, location);
		attach.sendPositionPacket(player, location);
	}
	
	public CraftProtocol[] getPackets()
	{
		return new CraftProtocol[]{ horse, skull, attach };
	}
	
	public CraftHorse getHorse()
	{
		return horse;
	}
	
	public CraftWitherSkull getWitherSkull()
	{
		return skull;
	}
	
	public AttachEntity getAttachEntity()
	{
		return attach;
	}
}
