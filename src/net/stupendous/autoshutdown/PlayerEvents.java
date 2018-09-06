package net.stupendous.autoshutdown;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class PlayerEvents implements Listener {

	private AutoShutdownPlugin plugin;
	
	public PlayerEvents(AutoShutdownPlugin plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		if(ShutdownTask.stopped) {
			
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("lobby");
			
			p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
			
		}
		
	}
	
}
