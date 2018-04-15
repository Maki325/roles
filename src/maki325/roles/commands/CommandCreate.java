package maki325.roles.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import maki325.roles.Roles;
import maki325.roles.api.Command;
import maki325.roles.utils.DB;

public class CommandCreate extends Command {

	public CommandCreate() {
		this.setName("create");
	}
	
	@Override
	protected void execute(Player player, String[] args) {

		if(args.length != 1) {
			commandUsage(player);
			return;
		}
		
		Roles instance = Roles.instance;
		
		if(instance.roles.containsValue(args[0])) {
        	player.sendMessage("That role already exists");
        	return;
		} else {
			DB.update("INSERT INTO `roles` VALUES ('" + UUID.randomUUID().toString() + "', '" + args[0] + "', '[" + args[0] + "]', '')");
        	Roles.instance.load();
		}
		
		return;
	}
	
	private void commandUsage(Player player) {
		player.sendMessage(ChatColor.RED + "Usage: /create <role name>");
	}
}
