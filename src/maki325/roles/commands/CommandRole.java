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
		if(!instance.hasRank(args[1])) {
			player.sendMessage(ChatColor.RED + "Role you requested does not exist");
			return;
		}
		
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("view")) {
				if(!instance.players.containsValue(instance.getRankUUID(args[1]))) {
					player.sendMessage(ChatColor.RED + "The role has no players in it");
					return;
				}
				player.sendMessage(ChatColor.GREEN + "Players in role " + args[1] + " are: " + ChatColor.RESET);
				for(UUID s:instance.players.keySet()) {
					if(instance.players.get(s).equals(instance.getRankUUID(name))) {
						player.sendMessage(" - " + Bukkit.getPlayer(s).getName());
					}
				}
				return;
			} else if(args[0].equalsIgnoreCase("reset")) {
				
				DB.update("DELETE FROM rolePairs WHERE roleid = '" + instance.getRankUUID(args[1]).toString() + "'" );
				
				for(UUID id:instance.players.keySet()) {
					if(instance.players.get(id).equals(instance.getRankUUID(args[1]))) {
						instance.players.remove(id);
					}
				}
				
				player.sendMessage(ChatColor.GREEN + "You removed all player from " + args[1] + " role");
				return;
			} else if(args[0].equalsIgnoreCase("destroy")) {
				DB.update("DELETE FROM rolePairs WHERE roleid = '" + instance.getRankUUID(args[1]).toString() + "'" );
				DB.update("DELETE FROM roles WHERE uuid = '" + instance.getRankUUID(args[1]).toString() + "'" );
				
				instance.ranks.remove(instance.getRankUUID(args[1]));
				
				for(UUID id:instance.players.keySet()) {
					if(instance.players.get(id).equals(instance.getRankUUID(args[1]))) {
						instance.players.remove(id);
					}
				}
			} else {
				commandUsage(player);
				return;
			}
		}
		
		if(args.length == 3) {
			if(args[0].equalsIgnoreCase("add")) {
				
				DB.update("DELETE FROM rolePairs WHERE playerid = '" + Bukkit.getPlayer(args[2]).getUniqueId().toString() + "'" );
				
				DB.update("INSERT INTO rolePairs VALUES ('" + UUID.randomUUID().toString() + "', '" + instance.getRankUUID(args[1]).toString() + "', '" + Bukkit.getPlayer(args[2]).getUniqueId().toString() + "')");
				
				instance.players.remove(Bukkit.getPlayer(args[2]).getUniqueId());
				instance.players.put(Bukkit.getPlayer(args[2]).getUniqueId(), instance.getRankUUID(args[1]));
				
				player.sendMessage(ChatColor.GREEN + "You added player " + args[2] + " to role " + args[1]);
				return;
			} else if(args[0].equalsIgnoreCase("remove")) {
				
				DB.update("DELETE FROM rolePairs WHERE playerid = '" + Bukkit.getPlayer(args[2]).getUniqueId().toString() + "'" );
				DB.update("INSERT INTO rolePairs VALUES ('" + UUID.randomUUID().toString() + "', '" + instance.getRankUUID(instance.startingRole).toString() + "', '" + Bukkit.getPlayer(args[2]).getUniqueId().toString() + "')");
				
				instance.players.remove(Bukkit.getPlayer(args[2]).getUniqueId());
				
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
