package de.codebucket.holoapi.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import de.codebucket.holoapi.HoloAPI;

public class HoloListener implements Listener
{
	private Plugin plugin;
	private Hologram hologram;
	private PluginManager manager;
	
	protected HoloListener(Plugin plugin, Hologram hologram, PluginManager manager) 
	{
		this.plugin = plugin;
		this.hologram = hologram;
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PlayerJoinEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(!hologram.canSeenBy(e.getPlayer()))
			{
				this.handleUpdate(e.getPlayer());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PlayerChangedWorldEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			this.handleUpdate(e.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PlayerTeleportEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(e.getFrom().getWorld() == e.getTo().getWorld())
			{
				return;
			}
			
			this.handleUpdate(e.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PlayerRespawnEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(hologram.canSeenBy(e.getPlayer()))
			{
				this.handleUpdate(e.getPlayer());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PlayerKickEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(hologram.canSeenBy(e.getPlayer()))
			{
				hologram.hide(e.getPlayer());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PlayerQuitEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(hologram.canSeenBy(e.getPlayer()))
			{
				hologram.hide(e.getPlayer());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(PluginDisableEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(e.getPlugin().equals(plugin))
			{
				hologram.remove();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(WorldUnloadEvent e)
	{
		if(HoloAPI.getManager().isTracked(this))
		{
			if(e.getWorld().getName().equals(hologram.getWorldName()))
			{
				hologram.clearAllPlayerViews();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHologramUpdate(ChunkUnloadEvent e)
	{
		Chunk chunk = e.getChunk();
		if(HoloAPI.getManager().isTracked(this))
		{
			World world = e.getWorld();
			if(!world.getName().equals(hologram.getWorldName()))
			{
				return;
			}
			
			if(isLocationInChunk(hologram.getDefaultLocation(), chunk))
			{
				for(UUID ident : hologram.getPlayerViews().keySet())
				{
					Player pl = Bukkit.getPlayer(ident);
					if(pl != null)
					{
						Vector vector = hologram.getPlayerView(pl);
						if(isLocationInChunk(getLocation(vector, hologram.getWorldName()), chunk))
						{
							hologram.hide(pl);
						}
					}
				}
			}
		}
	}
	
	public Plugin getPlugin()
	{
		return plugin;
	}
	
	public Hologram getHologram()
	{
		return hologram;
	}
	
	public PluginManager getPluginManager()
	{
		return manager;
	}
	
	private void handleUpdate(final Player player)
	{
		if(!HoloAPI.getManager().isTracked(this))
		{
			return;
		}
		
		if(!hologram.canSeenBy(player))
		{
			hologram.show(player);
			hologram.updateDisplay(player);
			return;
		}
		
		final Vector loc = hologram.getLocationFor(player);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() 
		{
			@Override
			public void run() 
			{
				hologram.hide();
				if(!player.getWorld().getName().equals(hologram.getWorldName()))
				{
					return;
				}
				
				hologram.show(player, loc.getX(), loc.getY(), loc.getZ());
				hologram.updateDisplay(player);
			}

		}, 3L);
	}
	
	private boolean isLocationInChunk(Location loc, Chunk chunk)
	{
		return (loc.getWorld().getChunkAt(loc).equals(chunk));
	}
	
	private Location getLocation(Vector vector, String world)
	{
		return new Location(Bukkit.getWorld(world), vector.getX(), vector.getY(), vector.getZ());
	}
}
