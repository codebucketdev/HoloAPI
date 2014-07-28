package de.codebucket.holoapi;

import org.bukkit.plugin.PluginManager;

import de.codebucket.holoapi.api.HoloManager;

@SuppressWarnings("static-access")
public class HoloAPI
{
	private static HoloPlugin CORE;

    public static void setCore(HoloPlugin plugin) 
    {
        if(CORE != null)
        {
            throw new RuntimeException("Core already set!");
        }
        
        CORE = plugin;
    }

    public static HoloPlugin getCore() 
    {
        return HoloAPI.CORE;
    }
    
	public static HoloManager getManager() 
    {
        return getCore().HOLO_MANAGER;
    }
    
	public static PluginManager getHandler()
    {
    	return getCore().PLUGIN_MANAGER;
    }
}
