package com.justbru00.timingsystem.bluemap;

import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.justbru00.timingsystem.bluemap.utils.Messager;
import com.justbru00.timingsystem.bluemap.bstats.BStats;

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
		Messager.msgConsole("&6Starting TimingSystemBlueMap " + getDescription().getVersion() + "...");
		
		Plugin timingSystem = Bukkit.getPluginManager().getPlugin("TimingSystem");
		if (timingSystem == null) {
			// TimingSystem isn't installed.
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		} else {
			// TimingSystem version check
			if (!timingSystem.getDescription().getVersion().contains("1.2")) {
				Messager.msgConsole("&cTimingSystemBlueMap only supports TimingSystem version 1.2 at this time. The plugin will attempt to run but you may encounter errors.");
			}	
		}
		
		BStats metrics = new BStats(this, BSTATS_PLUGIN_ID);
		String timingSystemVersion = timingSystem.getDescription().getVersion();
		metrics.addCustomChart(new BStats.SimplePie("timingsystem_version", () -> timingSystemVersion));
		
		BlueMapAPI.onEnable(api -> {
			// When BlueMap is enabled, run this code. Also works for /bluemap reload.
			MarkerSet markerSet = MarkerSet.builder().label("TimingSystem Track Locations").toggleable(true).build();
			
			for (Track track : TimingSystemAPI.getTracks()) {
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
