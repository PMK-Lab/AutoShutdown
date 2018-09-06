package net.stupendous.autoshutdown;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TreeSet;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.stupendous.autoshutdown.misc.Log;
import net.stupendous.autoshutdown.misc.Util;

public class AutoShutdownPlugin extends org.bukkit.plugin.java.JavaPlugin
{
  public String pluginName;
  public Log log;
  
  public AutoShutdownPlugin() {}
  
  protected ShutdownScheduleTask task = null;
  protected Timer backgroundTimer = null;
  protected Timer shutdownTimer = null;
  protected org.bukkit.scheduler.BukkitScheduler scheduler = null;
  protected boolean shutdownImminent = false;
  protected TreeSet<Calendar> shutdownTimes = new TreeSet<>();
  protected ArrayList<Integer> warnTimes = new ArrayList<>();
  
  SettingsManager settings = SettingsManager.getInstance();
  
  public void onDisable() {
    shutdownImminent = false;
    
    if (backgroundTimer != null) {
      backgroundTimer.cancel();
      backgroundTimer.purge();
      backgroundTimer = null;
    }
    
    if (shutdownTimer != null) {
      shutdownTimer.cancel();
      shutdownTimer.purge();
      shutdownTimer = null;
    }
    
    log.info("%s disabled.", new Object[] { settings.getDesc().getFullName() });
  }
  
  public void onEnable() {
    pluginName = getDescription().getName();
    log = new Log(pluginName);
    
    settings.setup(this);
    
    scheduler = getServer().getScheduler();
    shutdownImminent = false;
    shutdownTimes.clear();
    
    CommandExecutor autoShutdownCommandExecutor = new AutoShutdownCommand(this);
    getCommand("autoshutdown").setExecutor(autoShutdownCommandExecutor);
    getCommand("as").setExecutor(autoShutdownCommandExecutor);
    
    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    
    scheduleAll();
    
    Util.init(this, log);
    
    if (backgroundTimer != null) {
      backgroundTimer.cancel();
      backgroundTimer.purge();
      backgroundTimer = null;
    }
    
    backgroundTimer = new Timer();
    
    if (shutdownTimer != null) {
      shutdownTimer.cancel();
      shutdownTimer.purge();
      shutdownTimer = null;
    }
    
    Calendar now = Calendar.getInstance();
    now.set(13, 0);
    now.add(12, 1);
    
    now.add(14, 50);
    try {
      backgroundTimer.scheduleAtFixedRate(new ShutdownScheduleTask(this), now.getTime(), 60000L);
    } catch (Exception e) {
      log.severe("Failed to schedule AutoShutdownTask: %s", new Object[] { e.getMessage() });
    }
    
    
    log.info(pluginName + " enabled!");
  }
  
  protected void scheduleAll() {
    shutdownTimes.clear();
    warnTimes.clear();
    
    String[] shutdownTimeStrings = null;
    try
    {
      shutdownTimeStrings = settings.getConfig().getString("times.shutdowntimes").split(",");
    } catch (Exception e) {
      shutdownTimeStrings[0] = settings.getConfig().getString("times.shutdowntimes");
    }
    try { @SuppressWarnings("unused")
	Object localObject;
      for (String timeString : shutdownTimeStrings) {
        localObject = scheduleShutdownTime(timeString);
      }
      

      String[] strings = getConfig().getString("times.warntimes").split(",");
      for (String warnTime : strings)
        warnTimes.add(Integer.decode(warnTime));
    } catch (Exception e) {
      log.severe("Unable to configure Auto Shutdown using the configuration file.");
      log.severe("Is the format of shutdowntimes correct? It should be only HH:MM.");
      log.severe("Error: %s", new Object[] { e.getMessage() });
    }
  }
  
  protected Calendar scheduleShutdownTime(String timeSpec) throws Exception {
    if (timeSpec == null) {
      return null;
    }
    if (timeSpec.matches("^now$")) {
      Calendar now = Calendar.getInstance();
      int secondsToWait = getConfig().getInt("times.gracetime", 20);
      now.add(13, secondsToWait);
      
      shutdownImminent = true;
      shutdownTimer = new Timer();
      
      for (Integer warnTime : warnTimes) {
        long longWarnTime = warnTime.longValue() * 1000L;
        
        if (longWarnTime <= secondsToWait * 1000) {
          shutdownTimer.schedule(new WarnTask(this, warnTime.longValue()), secondsToWait * 1000 - 
            longWarnTime);
        }
      }
      

      shutdownTimer.schedule(new ShutdownTask(this), now.getTime());
      net.stupendous.autoshutdown.misc.Util.broadcast("The server has been scheduled for immediate shutdown.");
      
      return now;
    }
    
    if (!timeSpec.matches("^[0-9]{1,2}:[0-9]{2}$")) {
      throw new Exception("Incorrect time specification. The format is HH:MM in 24h time.");
    }
    
    Calendar now = Calendar.getInstance();
    Calendar shutdownTime = Calendar.getInstance();
    
    String[] timecomponent = timeSpec.split(":");
    shutdownTime.set(11, Integer.valueOf(timecomponent[0]).intValue());
    shutdownTime.set(12, Integer.valueOf(timecomponent[1]).intValue());
    shutdownTime.set(13, 0);
    shutdownTime.set(14, 0);
    
    if (now.compareTo(shutdownTime) >= 0) {
      shutdownTime.add(5, 1);
    }
    
    shutdownTimes.add(shutdownTime);
    
    return shutdownTime;
  }
  
  public void kickAll() {
    if (!getConfig().getBoolean("kickonshutdown", true)) {
      return;
    }
    
    log.info("Kicking all players ...");
    log.info(settings.getConfig().getString("messages.kickreason"));
    
    @SuppressWarnings("deprecation")
	Player[] players = getServer().getOnlinePlayers();
    
    for (Player player : players) {
      log.info("Kicking player %s.", new Object[] { player.getName() });
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Connect");
      out.writeUTF("lobby");
		
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
  }
  
  public SettingsManager getSettings() {
    return settings;
  }
}
