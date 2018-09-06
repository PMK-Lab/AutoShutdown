package net.stupendous.autoshutdown;

import java.util.Calendar;
import java.util.Timer;

public class ShutdownScheduleTask extends java.util.TimerTask
{
  protected AutoShutdownPlugin plugin = null;
  
  ShutdownScheduleTask(AutoShutdownPlugin instance) {
    plugin = instance;
  }
  
  public void run() {
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      public void run() {
        ShutdownScheduleTask.this.runTask();
      }
    });
  }
  
  private void runTask() {
    if (plugin.shutdownImminent) {
      return;
    }
    Calendar now = Calendar.getInstance();
    
    long firstWarning = ((Integer)plugin.warnTimes.get(0)).intValue() * 1000;
    
    for (Calendar cal : plugin.shutdownTimes) {
      if (cal.getTimeInMillis() - now.getTimeInMillis() <= firstWarning) {
        plugin.shutdownImminent = true;
        plugin.shutdownTimer = new Timer();
        
        for (Integer warnTime : plugin.warnTimes) {
          long longWarnTime = warnTime.longValue() * 1000L;
          
          if (longWarnTime <= cal.getTimeInMillis() - now.getTimeInMillis()) {
            plugin.shutdownTimer.schedule(new WarnTask(plugin, warnTime.longValue()), cal.getTimeInMillis() - 
              now.getTimeInMillis() - longWarnTime);
          }
        }
        

        plugin.shutdownTimer.schedule(new ShutdownTask(plugin), cal.getTime());
        
        net.stupendous.autoshutdown.misc.Util.broadcast(plugin.settings.config.getString("messages.shutdownmessage") + " à %s", 
          new Object[] { cal.getTime().toString() });
        
        break;
      }
    }
  }
}
