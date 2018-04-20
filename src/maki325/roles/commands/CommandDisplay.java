package maki325.roles.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import maki325.roles.Roles;
import maki325.roles.api.Command;
import maki325.roles.utils.DB;

public class CommandDisplay extends Command {

	public CommandDisplay() {
		this.setName("display");
		
		this.setPermission("roles.command.display");
		
	}
	
	@Override
	protected void execute(Player player, String[] args) {
		
		if(args.length < 2 || args.length > 4) {
			player.sendMessage(ChatColor.RED + "Usage: /display <set|view> <role> <args>");
			return;
		}

		Roles instance = Roles.instance;
		if(args[0].equalsIgnoreCase("view")) {
			if(args.length != 2) {
				player.sendMessage(ChatColor.RED + "Usage: /display <set|view> <role> <args>");
				return;
			}
			if(!instance.hasRank(args[1])) {
				player.sendMessage(ChatColor.RED + "Role you requested does not exist");
				return;
			}
			
			player.sendMessage("Role display is: " + ChatColor.translateAlternateColorCodes('&', instance.ranks.get(instance.getRankUUID(args[1])).getName() + " ExampleName"));
			
		} if(args[0].equalsIgnoreCase("set")) {
			if(args.length != 3) {
				player.sendMessage(ChatColor.RED + "Usage: /display <set|view> <role> <prefix>");
				return;
			}
			if(!instance.hasRank(args[1])) {
				player.sendMessage(ChatColor.RED + "Role you requested does not exist");
				return;
			}
			
			DB.update("UPDATE roles SET prefix = '" + args[2] + "' WHERE uuid = '" + instance.getRankUUID(args[1]).toString() + "'" );
			instance.ranks.get(instance.getRankUUID(args[1])).setPrefix(args[2]);
			
		}
		
		return;
	}

	
	
}
