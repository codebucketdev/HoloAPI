package de.codebucket.holoapi;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.codebucket.holoapi.api.HoloManager;

public class HoloPlugin extends JavaPlugin 
{
	protected static HoloPlugin CORE_INSTANCE;
	protected static HoloManager HOLO_MANAGER;
	protected static PluginManager PLUGIN_MANAGER;
	
	@Override
	public void onEnable() 
	{
		HoloAPI.setCore(this);
		if(Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib").isEnabled())
		{
			HoloPlugin.HOLO_MANAGER = new HoloManager();
			HoloPlugin.PLUGIN_MANAGER = this.getServer().getPluginManager();
			
			//INFO
			String v = getDescription().getVersion();
			String au = getDescription().getAuthors().get(0);
			getLogger().info("Version " + v + " by " + au + ".");
		}
		else
		{
			//ERROR
			getLogger().severe("ProtocolLib required! Get it at http://dev.bukkit.org/bukkit-plugins/protocollib/");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable() 
	{
		if(HOLO_MANAGER != null)
		{
            HOLO_MANAGER.clearAll();
        }
		this.getServer().getScheduler().cancelTasks(this);
		
		getLogger().info("Thank you for using HoloAPI in these plugins:");
		List<Plugin> plugins = new ArrayList<Plugin>();
		for(Plugin p : Bukkit.getPluginManager().getPlugins())
		{
			plugins.add(p);
		}
		getLogger().info(getPlugins(plugins));
	}
	
	private String getPlugins(List<Plugin> plugins)
	{
		String pmsg = "";
		for(Plugin p : plugins)
		{
			if(p.getDescription().getSoftDepend() != null)
			{
				if(p.getDescription().getSoftDepend().contains("HoloAPI"))
				{
					if(pmsg.length() == 0)
			    	{
						pmsg = (pmsg + p.getDescription().getName());
			    	}
			    	else
			    	{
			    		pmsg = (pmsg + ", " + p.getDescription().getName());
			    	}
				}
			}
			else if(p.getDescription().getDepend() != null)
			{
				if(p.getDescription().getDepend().contains("HoloAPI"))
				{
					if(pmsg.length() == 0)
			    	{
						pmsg = (pmsg + p.getDescription().getName());
			    	}
			    	else
			    	{
			    		pmsg = (pmsg + ", " + p.getDescription().getName());
			    	}
				}
			}
		}
		
		if(pmsg.length() == 0)
		{
			pmsg = "[ No plugins were found! ]";
		}
		return pmsg;
	}
}
