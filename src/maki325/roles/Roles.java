package maki325.roles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import maki325.roles.commands.CommandCreate;
import maki325.roles.commands.CommandDisplay;
import maki325.roles.commands.CommandPermissions;
import maki325.roles.commands.CommandRefresh;
import maki325.roles.commands.CommandRole;
import maki325.roles.events.PlayerJoinEvent;
import maki325.roles.events.PlayerMessageEvent;
import maki325.roles.utils.DB;

public class Roles extends JavaPlugin {

	//roleid, displayName(role)
	public HashMap<UUID, String> roles = new HashMap<UUID, String>();
	//role display name, prefix(role)
	public HashMap<String, String> rolesPrefix = new HashMap<String, String>();
	//Role PlayerUUID, displayName(Rank)
	public HashMap<UUID, String> playerRoles = new HashMap<UUID, String>();
	//uuid(player), displayName(player)
	public HashMap<UUID, String> players = new HashMap<UUID, String>();
	//role, perms
	public HashMap<String, List<String>> rolePerms = new HashMap<String, List<String>>();
	
	public String startingRole = "";
	
	public Logger logger;
	
	public static Roles instance;
	
	//public static Permissions permissions;
	
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
        System.out.println("Start role: " + startingRole);
        
        host = getConfig().getString("database.host");
        port = getConfig().getInt("database.port");
        username = getConfig().getString("database.username");
        database = getConfig().getString("database.database");
        password = getConfig().getString("database.password");
        
        if(host == "" || port == -1 || username == "" || database == "" || password == null) {
        	logger.log(Level.SEVERE, "Something isnt right in the database section in the config file");
        	this.getPluginLoader().disablePlugin(this);
        	return;
        }
        
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
		roles.put(id, startingRole);
		rolePerms.put(startingRole, new ArrayList<String>());
		rolesPrefix.put(startingRole, "&f[" + startingRole.toUpperCase() + "]&r");
	}
	
	public void load() {
		ResultSet role = DB.query("SELECT * FROM `mc_4188`.`roles`");
        while(DB.next(role)) {
        	roles.put(UUID.fromString((String) DB.value(role, "uuid")), (String) DB.value(role, "displayName"));
        	rolePerms.put((String) DB.value(role, "displayName"), new ArrayList<String>(Arrays.asList(((String) DB.value(role, "permissions")).split(";"))));
        	rolesPrefix.put((String) DB.value(role, "displayName"), (String) DB.value(role, "prefix"));
        }
        
        ResultSet playerRank = DB.query("SELECT * FROM `mc_4188`.`rolePairs`");
        while(DB.next(playerRank)) {
        	playerRoles.put(UUID.fromString((String) DB.value(playerRank, "playerid")), roles.get(UUID.fromString((String) DB.value(playerRank, "roleid"))));
        }
		
        ResultSet player = DB.query("SELECT * FROM `mc_4188`.`players`");
        while(DB.next(player)) {
        	players.put(UUID.fromString((String) DB.value(player, "uuid")), (String) DB.value(player, "displayName"));
        	//logger.log(Level.INFO, "Player: " + UUID.fromString((String) DB.value(player, "uuid")) + " | " + (String) DB.value(player, "displayName"));
        }
        
        if(!roles.containsValue(startingRole)) {
			createDeafult();
		}
	}
	
	public UUID uuidFormName(String name) {
		for(UUID u:roles.keySet()) {
			if(roles.get(u).equals(name)) {
				return u;
			}
		}
		return null;
	}
	
	public boolean hasRolePermPlayer(Player p, String permission) {
		if(permission == null) { System.out.println("NULL perm"); return false; }
		if(p == null) { System.out.println("NULL p"); return false; }
		String role = getPlayerRole(p);
		if(role == null) { System.out.println("NULL role"); return false; }
		List<String> perms = getRolePerms(role);
		if(perms == null) { System.out.println("NULL perms-m"); return false; }
		return perms.contains(permission);
		//return getRolePerms(getPlayerRole(p)).contains(permission);
	}
	
	public List<String> getRolePerms(String role) {
		return rolePerms.get(role);
	}
	
	public String getPlayerRole(Player p) {
		return playerRoles.get(p.getUniqueId());
	}
	
}
