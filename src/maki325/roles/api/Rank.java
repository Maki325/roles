package maki325.roles.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class Rank {
	
	private String name;
	private List<String> permissions;
	private String prefix;

	public Rank(String name) {
		this(name, "", new ArrayList<String>());
	}
	
	public Rank(String name, String prefix) {
		this(name, prefix, new ArrayList<String>());
	}
	
	public Rank(String name, List<String> permissions) {
		this(name, "", permissions);
	}
	
	public Rank(String name, String... permissions) {
		this(name, "", permissions);
	}
	
	public Rank(String name, String prefix, String... permissions) {
		this(name, prefix, Arrays.asList(permissions));
	}
	
	public Rank(String name, String prefix, List<String> permissions) {
		this.name = name;
		this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
		this.permissions = permissions;
	}
	
	public boolean hasPermission(String... permission) {
		for(String perm:permission) {
			if(!permissions.contains(perm)) {
				return false;
			}
		}
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void addPermission(String... permissions) {
		this.permissions.addAll(Arrays.asList(permissions));
	}
	
	public void removePermission(String... permissions) {
		this.permissions.removeAll(Arrays.asList(permissions));
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public static boolean isSimilar(Rank a, Rank b) {
		return a.getName().equals(b.getName());
	}
	
	public boolean isSimilar(Rank a) {
		return isSimilar(this, a);
	}
	
}
