package maki325.roles.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import maki325.roles.Roles;
import maki325.roles.api.Command;

public class CommandRefresh extends Command {

	public CommandRefresh() {
		this.setName("refresh");
		
		this.setPermission("roles.command.refresh");
	}
	
	@Override
	protected void execute(Player player, String[] args) {
		if(args.length != 0) {
			player.sendMessage(ChatColor.RED + "Usage /refresh");
			return;
		}

		Roles.instance.reloadConfig();
		
		Roles.instance.load();
		
		player.sendMessage(ChatColor.GREEN + "REFRESH COMPLETE");
		
	}

}
