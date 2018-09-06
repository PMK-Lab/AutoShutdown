package net.stupendous.autoshutdown;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager
{
  Plugin p;
  FileConfiguration config;
  File cfile;
  
  private SettingsManager() {}
  
  static SettingsManager instance = new SettingsManager();
  
  public static SettingsManager getInstance() {
    return instance;
  }
  
  public void setup(Plugin p) {
    this.p = p;
    config = p.getConfig();
    p.saveDefaultConfig();
    cfile = new File(p.getDataFolder(), "config.yml");
  }
  
  public FileConfiguration getConfig() {
    return config;
  }
  
  public void saveConfig()
  {
    try {
      config.save(cfile);
    }
    catch (IOException e) {
      Bukkit.getServer().getLogger().severe(ChatColor.RED + "Unable to save configuration.");
    }
  }
  
  public void reloadConfig() {
    if (cfile.exists()) {
      config = YamlConfiguration.loadConfiguration(cfile);
    } else
      p.saveDefaultConfig();
  }
  
  public PluginDescriptionFile getDesc() {
    return p.getDescription();
  }
}
