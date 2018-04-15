package maki325.roles.api;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import maki325.roles.Roles;

public abstract class Command implements Listener {
	
	Roles instance;
	
	public void setup(JavaPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		this.instance = Roles.instance;
		permissionErrorMessage = ChatColor.translateAlternateColorCodes('&', permissionErrorMessage);
	}
	
	protected String name = null;
	protected ArrayList<String> aliases = new ArrayList<String>();
	protected String permission = null;
	protected String permissionErrorMessage = "&cYou don't have permission to do that.";
	
	protected void setPermissionErrorMessage(String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		permissionErrorMessage = msg;
	}
	
	protected void addAlias(String alias) {
		aliases.add(alias);
	}
	
	protected void setName(String _name) {
		name = _name;
	}
	
	protected void setPermission(String p) {
		permission = p;
	}
	
	protected abstract void execute(Player p, String[] args);
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {

		//Check if command has name yet
		if(name==null) {
			return;
		}
		
		String msg = e.getMessage().substring(1, e.getMessage().length());
		
		String[] _args = msg.split(" ");
		//Check name of command
		if(!_args[0].equalsIgnoreCase(name)) {
			Boolean match = false;
			for(int i = 0; i<aliases.size(); i++) {
				String alias = aliases.get(i);
				if(_args[0].equalsIgnoreCase(alias)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return;
			}
		}

		//Check if player has permission
		if(permission!=null) {
			if(!e.getPlayer().hasPermission(permission) || !instance.hasRolePermPlayer(e.getPlayer(), permission)) {
				e.getPlayer().sendMessage(permissionErrorMessage);
				return;
			}
		}
		
		//Set Event Cancel
		e.setCancelled(true);
		
		//Get arguments
		String[] args = new String[_args.length-1];
		for(int i = 1; i<_args.length; i++) {
			args[i-1] = _args[i];
		}
		
		//Execute
		execute(e.getPlayer(), args);
		
	}
	
}
