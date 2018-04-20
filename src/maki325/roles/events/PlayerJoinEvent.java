package maki325.roles.events;

import java.util.UUID;

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
        Roles instance = Roles.instance;
        if(!instance.players.containsKey(p.getUniqueId())) {
        	DB.update("INSERT INTO players (uuid,displayName) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "')");
        	DB.update("ISERT INTO rolePairs VALUES ('" + UUID.randomUUID().toString() + "','" + instance.getRankUUID(instance.startingRole) + "', '" + p.getUniqueId() + "')");
        	instance.players.put(p.getUniqueId(), instance.getRankUUID(instance.startingRole));
        }
    }
	
}
