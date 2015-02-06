package com.walrusone.skywars.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GameMap;

public class MapController {

	private File dataDirectory;
	private File maps;
	private File rootDirectory;
	private final Map<String, GameMap> mapList = Maps.newHashMap();
	private ArrayList<String> editMaps = new ArrayList<String>();
	
	public MapController() {
		dataDirectory = SkyWarsReloaded.get().getDataFolder();
		maps = new File (dataDirectory, "maps");
		String root = SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath();
		rootDirectory = new File(root);
		for (File map : maps.listFiles()) {
			if (map.isDirectory()) {
				registerMap(map.getName());
				if (mapExists(map.getName().toLowerCase())) {
					editMaps.add(map.getName().toLowerCase());
				}
			}
		}
	}
	
	public void registerMap(String name) {
		WorldController wc = SkyWarsReloaded.getWC();
		File source = new File(maps, name);
		File target = new File(rootDirectory, name);
		wc.copyWorld(source, target);
		wc.loadWorld(name);
		boolean loaded = false;
		for(World w: Bukkit.getServer().getWorlds()) {
		  if(w.getName().equals(name)) {
		    loaded = true;
		  }
		}
		if(loaded) {
		  GameMap gameMap = new GameMap(name, source);
		  	if (!gameMap.containsSpawns()) {
		  		if (name.equalsIgnoreCase("lobby")) {
		  			SkyWarsReloaded.get().getLogger().info("Could Not Register Map: " + name + " - Map must have at least 1 Spawn Point!");
		  		} else {
		  			SkyWarsReloaded.get().getLogger().info("Could Not Register Map: " + name + " - Map must have " + SkyWarsReloaded.get().getConfig().getInt("gameVariables.numberOfSpawns") + " Spawn Points");
		  		}
			} else {
		  		mapList.put(name.toLowerCase(), gameMap);
		  		SkyWarsReloaded.get().getLogger().info("Registered Map " + name + "!");
		  	}
		} else {
			SkyWarsReloaded.get().getLogger().info("Could Not Load Map: " + name);
			wc.deleteWorld(target);
		}
		wc.unloadWorld(name);
		wc.deleteWorld(target);
	}
	
	public GameMap getMap(String name){
		return mapList.get(name);
	}
	
	public GameMap removeMap(String name){
		return mapList.remove(name);
	}

	public List<String> getMaps() {
		return new ArrayList<String>(mapList.keySet());
	}
	
	public void addEditMap(String name) {
		editMaps.add(name);
	}
	
	public ArrayList<String> getEditMaps() {
		return editMaps;
	}
	
	public boolean mapExists(String name) {
		if (editMaps.contains(name)) {
			return true;
		}
		return false;
	}
	
	public boolean mapRegistered(String name) {
		if (mapList.containsKey(name)) {
			return true;
		}
		return false;
	}
}
