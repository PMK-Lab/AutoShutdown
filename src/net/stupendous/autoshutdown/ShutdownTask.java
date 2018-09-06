package net.stupendous.autoshutdown;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.stupendous.autoshutdown.misc.Log;

public class ShutdownTask extends TimerTask
{
  protected AutoShutdownPlugin plugin = null;
  protected Log log = null;
  
  public static boolean stopped = false;
  
  ShutdownTask(AutoShutdownPlugin instance) {
    plugin = instance;
    log = plugin.log;
  }
  
  public void run() {
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      public void run() {
        log.info("Shutdown in progress.");
        
        plugin.kickAll();
        
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				plugin.getServer().savePlayers();
		        Server server = plugin.getServer();
		        
		        server.savePlayers();
		        
		        for (World world : server.getWorlds()) {
		          world.save();
		          server.unloadWorld(world, true);
		        }
		        
		        server.shutdown();
				
			}
		}, 360);
		
      }
    });
  }
}
