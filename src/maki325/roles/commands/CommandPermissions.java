package maki325.roles.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import maki325.roles.Roles;
import maki325.roles.api.Command;
import maki325.roles.utils.DB;
import maki325.roles.utils.Utils;

public class CommandPermissions extends Command {

	public CommandPermissions() {
		this.setName("permission");
		
		this.setPermission("roles.command.permission");
	}
	
	@Override
	protected void execute(Player player, String[] args) {
		
		if(args.length < 2) {
			commandUsage(player);
			return;
		}
		Roles instance = Roles.instance;
		
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("reset")) { // RESETING ROLE PERMISSIONS
				if(!instance.roles.containsValue(args[1])) {
					player.sendMessage(ChatColor.RED + "Role you requested does not exist");
					return;
				}
				
				DB.update("UPDATE roles SET permissions = '' WHERE uuid = '" + instance.uuidFormName(args[1]).toString() + "'");
	
				instance.load();
				
				player.sendMessage(ChatColor.GREEN + "You reset permissions to role " + args[1]);
				return;
			} else if(args[0].equalsIgnoreCase("view")) {
				if(!instance.roles.containsValue(args[1])) {
					player.sendMessage(ChatColor.RED + "Role you requested does not exist");
					return;
				}
				
				player.sendMessage(ChatColor.GREEN + "Premissions for role " + args[1] + " are: ");
				for(String s:instance.rolePerms.get(args[1])) {
					player.sendMessage(ChatColor.GREEN + " - " + s);
				}
				return;
			}
		}

		if(args[0].equalsIgnoreCase("add")) {
			if(!instance.roles.containsValue(args[1])) {
				player.sendMessage(ChatColor.RED + "Role you requested does not exist");
				return;
			}
			
			List<String> perms = instance.rolePerms.get(args[1]);
			String ret = "";
			for(int i = 2;i < args.length;i++) {
				ret += args[i] + " ";
				perms.add(args[i]);
			}
			
			DB.update("UPDATE roles SET permissions = '" + Utils.listToString(perms, ";") + "' WHERE uuid = '" + instance.uuidFormName(args[1]).toString() + "'");

			instance.load();
			
			player.sendMessage(ChatColor.GREEN + "You added premissions: " + ret + "to role: " + args[1]);
			return;
		} else if(args[0].equalsIgnoreCase("remove")) {
			if(!instance.roles.containsValue(args[1])) {
				player.sendMessage(ChatColor.RED + "Role you requested does not exist");
				return;
			}
			
			List<String> perms = instance.rolePerms.get(args[1]);
			String ret = "";
			for(int i = 2;i < args.length;i++) {
				ret += args[i] + " ";
				perms.remove(args[i]);
			}
			
			DB.update("UPDATE roles SET permissions = '" + Utils.listToString(perms, ";") + "' WHERE uuid = '" + instance.uuidFormName(args[1]).toString() + "'");

			instance.load();

			player.sendMessage(ChatColor.GREEN + "You removed premissions: " + ret + "from role " + args[1]);
			return;
		} else {
			commandUsage(player);
			return;
		}
	}

	private void commandUsage(Player player) {
		player.sendMessage(ChatColor.RED + "Usage: /permission <add|remove|view|reset> <Name> <premissions> ...");
	}
	
	
}
