package maki325.roles.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import maki325.roles.Roles;
import net.md_5.bungee.api.ChatColor;

public class PlayerMessageEvent implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	private void onMessage(AsyncPlayerChatEvent event) {
		String format = String.format(Roles.instance.getConfig().getString("message.format"), event.getPlayer().getDisplayName(), ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		event.setFormat(format);
	}
	
}
