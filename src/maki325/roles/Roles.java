package maki325.roles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import maki325.roles.api.Rank;
import maki325.roles.commands.CommandCreate;
import maki325.roles.commands.CommandDisplay;
import maki325.roles.commands.CommandPermissions;
import maki325.roles.commands.CommandRefresh;
import maki325.roles.commands.CommandRole;
import maki325.roles.events.PlayerJoinEvent;
import maki325.roles.events.PlayerMessageEvent;
import maki325.roles.utils.DB;

public class Roles extends JavaPlugin {

	public HashMap<UUID, Rank> ranks = new HashMap<UUID, Rank>();
	public HashMap<UUID, UUID> players = new HashMap<UUID, UUID>();
	
	public String startingRole = "";
	
	public Logger logger;
	
	public static Roles instance;
	
	private String host = null;
	private int port = -1;
	private String username = null;
	private String database = null;
	private String password = null;
	private Connection connection = null;
	
	@Override
	public void onEnable() {

		logger = getLogger();
		saveDefaultConfig();

		logger.log(Level.INFO, "Registering Instance");
		instance = this;
		
		logger.log(Level.INFO, "Setting The DB Up");
        
        saveDefaultConfig();
        
        startingRole = getConfig().getString("roles.start");
        
		setupDB();
		
        load();
        
		logger.log(Level.INFO, "Registering Events");

		logger.log(Level.INFO, "Registering Playaer Login Event");
		getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
		
		logger.log(Level.INFO, "Registering Player Message Event");
		getServer().getPluginManager().registerEvents(new PlayerMessageEvent(), this);
		
		logger.log(Level.INFO, "Events Registerd");

		
		logger.log(Level.INFO, "Registering Commands");
		
		logger.log(Level.INFO, "Registering Create Command and regestering Command Event");
		CommandCreate createCommand = new CommandCreate();
		createCommand.setup(this);
		
		logger.log(Level.INFO, "Registering Display Command and regestering Command Event");
		CommandDisplay displayCommand = new CommandDisplay();
		displayCommand.setup(this);

		logger.log(Level.INFO, "Registering Permissions Command and regestering Command Event");
		CommandPermissions permissionCommand = new CommandPermissions();
		permissionCommand.setup(this);

		logger.log(Level.INFO, "Registering Role Command and regestering Command Event");
		CommandRole roleCommand = new CommandRole();
		roleCommand.setup(this);

		logger.log(Level.INFO, "Registering Refresh Command and regestering Command Event");
		CommandRefresh refreshCommand = new CommandRefresh();
		refreshCommand.setup(this);
		
	}
	
	@Override
	public void onDisable() {
		saveConfig();
		ranks.clear();
		players.clear();
	}
	
	public void setupDB() {
		
		synchronized (this){
			try {
				
				synchronized(this) {
					if(getConnection()!=null && !getConnection().isClosed()) {
						return;
					}
					Class.forName("com.mysql.jdbc.Driver");
					setConnection(DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,username,password));
				}
				
			} catch (SQLException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void createDeafult() {
		UUID id = UUID.randomUUID();
        System.out.println("Start role 1: " + startingRole);
		DB.update("INSERT INTO `roles` VALUES ('" + id.toString() + "', '" + startingRole + "', '&f[" + startingRole.toUpperCase() + "]&r', '')");
		ranks.put(id, new Rank(startingRole, "", new ArrayList<String>()));
	}
	
	public void load() {
		ResultSet role = DB.query("SELECT * FROM `roles`");
        while(DB.next(role)) {
        	Rank temp = new Rank((String) DB.value(role, "displayName"), (String) DB.value(role, "prefix"), ((String) DB.value(role, "permissions")).split(";"));
			ranks.put(UUID.fromString((String) DB.value(role, "uuid")), temp);
        }
        
        ResultSet playerRank = DB.query("SELECT * FROM `rolePairs`");
        while(DB.next(playerRank)) {
        	players.put(UUID.fromString((String) DB.value(playerRank, "playerid")), UUID.fromString((String) DB.value(playerRank, "roleid")));
        }

        if(!hasRank(startingRole)) {
			createDeafult();
		}
	}
	
	public UUID getRankUUID(String name) {
		for(UUID u:ranks.keySet()) {
			if(ranks.get(u).getName().equals(name)) {
				return u;
			}
		}
		return null;
	}
	
	public boolean hasRank(String name) {
		for(UUID u:ranks.keySet()) {
			if(ranks.get(u).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRolePermPlayer(Player p, String... permission) {
		return ranks.get(players.get(p.getUniqueId())).hasPermission(permission);
	}
	
}
