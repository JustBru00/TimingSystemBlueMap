package com.justbru00.timingsystem.bluemap;

import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.justbru00.timingsystem.bluemap.utils.Messager;
import com.justbru00.timingsystem.bluemap.bstats.BStats;
import com.justbru00.timingsystem.bluemap.configuration.ConfigurationManager;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.makkuusen.timing.system.track.Track;

/**
 * TimingSystemBlueMap - This plugin will display track spawn locations as poi markers on bluemap.
 *   Copyright (C) 2023 Justin "JustBru00" Brubaker
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * @author Justin Brubaker
 *
 */
public class TimingSystemBlueMapPlugin extends JavaPlugin {
	
	private static TimingSystemBlueMapPlugin instance;
	private static final int BSTATS_PLUGIN_ID = 18483;
	private static final String[] TIMING_SYSTEM_SUPPORTED_VERSIONS = {"1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8"};
	
	public static String PLUGIN_VERSION = null;    
	public static ConsoleCommandSender clogger = Bukkit.getServer().getConsoleSender();
	public static Logger log = Bukkit.getLogger();
	
	public static String prefix = Messager.color("&8[&bTimingSystem&fBlueMap&8] &7");

	@Override
	public void onDisable() {
		Messager.msgConsole("&6Disabled plugin.");
		instance = null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		
		PLUGIN_VERSION = TimingSystemBlueMapPlugin.getInstance().getDescription().getVersion();
		Messager.msgConsole("&6Starting TimingSystemBlueMap " + PLUGIN_VERSION + "...");		
		
		saveDefaultConfig();
		
		if (ConfigurationManager.doesConfigYmlNeedUpdated()) {
			Messager.msgConsole("&c[WARN] The config.yml file version is incorrect. TimingSystemBlueMap v" + PLUGIN_VERSION +
					" expects a config.yml version of " + ConfigurationManager.CONFIG_VERSION + 
					". Attempting to add missing values to the config file.");
			ConfigurationManager.updateConfigYml();
		}
		
		Plugin timingSystem = Bukkit.getPluginManager().getPlugin("TimingSystem");
		if (timingSystem == null) {
			// TimingSystem isn't installed.
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		} else {
			// TimingSystem version check
			String timingSystemVersion = timingSystem.getDescription().getVersion();
			boolean supportedVersion = false;
			for (String version : TIMING_SYSTEM_SUPPORTED_VERSIONS) {
				if (timingSystemVersion.contains(version)) {
					supportedVersion = true;
					break;
				}
			}		
			
			if (!supportedVersion) {
				Messager.msgConsole("&cTimingSystemBlueMap version " + PLUGIN_VERSION + " doesn't support TimingSystem version "
			+ timingSystemVersion + ". The add-on will attempt to run as normal, but you may encounter issues.");
			}
		}
		
		Plugin bluemap = Bukkit.getPluginManager().getPlugin("BlueMap");
		if (bluemap == null) {
			// BlueMap isn't installed.
			Messager.msgConsole("&cBlueMap isn't installed on this server. There is no point to have this addon.");
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		
		BStats metrics = new BStats(this, BSTATS_PLUGIN_ID);
		String timingSystemVersion = timingSystem.getDescription().getVersion();
		metrics.addCustomChart(new BStats.SimplePie("timingsystem_version", () -> timingSystemVersion));
		
		BlueMapAPI.onEnable(api -> {
			// When BlueMap is enabled, run this code. Also works for /bluemap reload.
			MarkerSet markerSet = MarkerSet.builder().label("TimingSystem Track Locations").toggleable(true).build();
			
			for (Track track : TimingSystemAPI.getTracks()) {
				// ISSUE #1
				if (TimingSystemBlueMapPlugin.getInstance().getConfig().getBoolean("poi_markers.tracks.spawn_locations.hide_closed_tracks")) {
					if (!track.isOpen()) {
						continue;
					}
				} // END ISSUE #1
				
				double x = track.getSpawnLocation().getX();
				double y = track.getSpawnLocation().getY();
				double z = track.getSpawnLocation().getZ();
					
				POIMarker marker = POIMarker.builder()
						.label(track.getCommandName())
						.detail("<p style=\"text-align: center;\">" + track.getCommandName() + "</p>"
						+ "<p style=\"text-align: center;\">Total Finishes: " + track.getTotalFinishes() + "</p>"
						+ "<p style=\"text-align: center;\">Total Attempts: " + track.getTotalAttempts() + "</p>")
						.position(x, y, z)
						.maxDistance(10000)
						.build();				
			
				Optional<BlueMapWorld> possibleWorld = api.getWorld(track.getSpawnLocation().getWorld());
				if (!possibleWorld.isPresent()) {
					Messager.msgConsole("&cBlueMapWorld wasn't able to be found... Skipping adding marker for track " + track.getCommandName());
					continue;
				}				
				BlueMapWorld blueWorld = possibleWorld.get();
				
				markerSet.getMarkers().put("tt-" + track.getCommandName(), marker);		
			
				for (BlueMapMap map : blueWorld.getMaps()) {
					map.getMarkerSets().put("timingsystem-bluemap-tracks", markerSet);
				}					
			}
		});
	}
	
	public static TimingSystemBlueMapPlugin getInstance() {
		return instance;
	}
}
