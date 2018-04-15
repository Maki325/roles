package maki325.roles.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import maki325.roles.Roles;
import maki325.roles.api.Command;
import maki325.roles.utils.DB;

public class CommandRole extends Command {

	public CommandRole() {
		this.setName("role");
		
		this.setPermission("roles.command.role");
	}
	
	@Override
	protected void execute(Player player, String[] args) {

		if(args.length < 2 || args.length > 3) {
			commandUsage(player);
			return;
		}
		
		Roles instance = Roles.instance;
		if(args.length == 2) {
			if(!instance.roles.containsValue(args[1])) {
				player.sendMessage(ChatColor.RED + "Role you requested does not exist");
				return;
			}
			if(args[0].equalsIgnoreCase("view")) {
				if(!instance.playerRoles.containsValue(args[1])) {
					player.sendMessage(ChatColor.RED + "The role has no players in it");
					return;
				}
				player.sendMessage(ChatColor.GREEN + "Players in role " + args[1] + " are: " + ChatColor.RESET);
				for(UUID s:instance.playerRoles.keySet()) {
					if(instance.playerRoles.get(s).equals(args[1])) {
						player.sendMessage(" - " + Bukkit.getPlayer(s).getName());
					}
				}
				return;
			} else if(args[0].equalsIgnoreCase("reset")) {
				
				DB.update("DELETE FROM rolePairs WHERE roleid = '" + instance.uuidFormName(args[1]).toString() + "'" );
				
				instance.load();
				
				player.sendMessage(ChatColor.GREEN + "You removed all player from " + args[1] + " role");
				return;
			} else if(args[0].equalsIgnoreCase("destroy")) {
				DB.update("DELETE FROM rolePairs WHERE roleid = '" + instance.uuidFormName(args[1]).toString() + "'" );
				DB.update("DELETE FROM roles WHERE uuid = '" + instance.uuidFormName(args[1]).toString() + "'" );

				instance.load();
			} else {
				commandUsage(player);
				return;
			}
		}
		
		if(args.length == 3) {
			if(!instance.roles.containsValue(args[1])) {
				player.sendMessage(ChatColor.RED + "Role you requested does not exist");
				return;
			}
			if(args[0].equalsIgnoreCase("add")) {
				
				DB.update("DELETE FROM rolePairs WHERE playerid = '" + Bukkit.getPlayer(args[0]).getUniqueId().toString() + "'" );
				
				DB.update("INSERT INTO rolePairs VALUES ('" + UUID.randomUUID().toString() + "', '" + instance.uuidFormName(args[2]).toString() + "', '" + Bukkit.getPlayer(args[0]).getUniqueId().toString() + "')");
				
				Player pl = Bukkit.getPlayer(args[0]);
				if(Bukkit.getOnlinePlayers().contains(pl)) {
					Bukkit.getPlayer(args[0]).setDisplayName(ChatColor.translateAlternateColorCodes('&', instance.rolesPrefix.get(args[2])) + " " + pl.getName());
					Bukkit.getPlayer(args[0]).setDisplayName(ChatColor.translateAlternateColorCodes('&', instance.rolesPrefix.get(args[2])) + " " + pl.getName());
				}
				
				instance.load();
				
				player.sendMessage(ChatColor.GREEN + "You added player " + args[2] + " to role " + args[1]);
				return;
			} else if(args[0].equalsIgnoreCase("remove")) {
				
				DB.update("DELETE FROM rolePairs WHERE roleid = '" + instance.uuidFormName(args[1]).toString() + "' AND playerid = '" + Bukkit.getPlayer(args[0]).getUniqueId().toString() + "'" );

				instance.load();
				
				player.sendMessage(ChatColor.GREEN + "You removed player " + args[2] + " from role " + args[1]);
				return;
			} else {
				commandUsage(player);
				return;
			}
		}
		
		
		
		return;
	}

	private void commandUsage(Player player) {
		player.sendMessage(ChatColor.RED + "Usage: /role <add|remove|view|reset> <role> <player>");
	}
	
}
