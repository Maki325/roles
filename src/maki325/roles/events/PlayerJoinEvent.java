package maki325.roles.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import maki325.roles.Roles;
import maki325.roles.utils.DB;

public class PlayerJoinEvent implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if(Roles.instance.players.containsKey(p.getUniqueId())) {
        	p.setDisplayName(ChatColor.translateAlternateColorCodes('&', Roles.instance.rolesPrefix.get(Roles.instance.playerRoles.get(p.getUniqueId()))) + " " + p.getName());
        	p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', Roles.instance.rolesPrefix.get(Roles.instance.playerRoles.get(p.getUniqueId()))) + " " + p.getName());
        } else {
        	DB.update("INSERT INTO players (uuid,displayName) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "')");
        	p.setDisplayName(ChatColor.translateAlternateColorCodes('&', Roles.instance.rolesPrefix.get(Roles.instance.startingRole)) + " " + p.getName());
        	p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', Roles.instance.rolesPrefix.get(Roles.instance.startingRole)) + " " + p.getName());
        	Roles.instance.load();
        }
    }
	
}
