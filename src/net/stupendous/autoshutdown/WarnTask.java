package net.stupendous.autoshutdown;

import java.util.concurrent.TimeUnit;
import net.stupendous.autoshutdown.misc.Log;
import net.stupendous.autoshutdown.misc.Util;
import org.bukkit.configuration.file.FileConfiguration;

public class WarnTask extends java.util.TimerTask
{
  protected final AutoShutdownPlugin plugin;
  protected final Log log;
  protected long seconds = 0L;
  
  public WarnTask(AutoShutdownPlugin plugin, long seconds) {
    this.plugin = plugin;
    log = log;
    this.seconds = seconds;
  }
  
  public void run() {
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      public void run() {
        if (TimeUnit.SECONDS.toMinutes(seconds) > 0L) {
          if (TimeUnit.SECONDS.toMinutes(seconds) == 1L) {
            if (seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds)) == 0L) {
              Util.broadcast(
                plugin.settings.config.getString("messages.shutdownmessage") + " in 1 " + plugin.settings.config.getString("messages.minute") + "...");
            } else {
              Util.broadcast(
              

                plugin.settings.config.getString("messages.shutdownmessage") + " in 1 " + plugin.settings.config.getString("messages.minute") + " %d " + plugin.settings.config.getString("messages.second") + "s ...", 
                new Object[] {
                Long.valueOf(seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))) });
            }
          }
          else if (seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds)) == 0L) {
            Util.broadcast(
              plugin.settings.config.getString("messages.shutdownmessage") + " in %d " + plugin.settings.config.getString("messages.minute") + "s ...", 
              new Object[] { Long.valueOf(TimeUnit.SECONDS.toMinutes(seconds)) });
          } else {
            Util.broadcast(
            

              plugin.settings.config.getString("messages.shutdownmessage") + " in %d " + plugin.settings.config.getString("messages.minute") + "s %d " + plugin.settings.config.getString("messages.second") + "s ...", 
              new Object[] {
              Long.valueOf(TimeUnit.SECONDS.toMinutes(seconds)), 
              Long.valueOf(seconds - 
              TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))) });
          }
        }
        else if (TimeUnit.SECONDS.toSeconds(seconds) == 1L) {
          Util.broadcast(plugin.settings.config.getString("messages.shutdownmessage") + " NOW!");
        } else {
          Util.broadcast(
            plugin.settings.config.getString("messages.shutdownmessage") + " in %d " + plugin.settings.config.getString("messages.second") + "s ...", 
            new Object[] { Long.valueOf(seconds) });
        }
      }
    });
  }
}
