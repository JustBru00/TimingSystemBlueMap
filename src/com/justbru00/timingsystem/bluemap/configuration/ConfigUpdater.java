package com.justbru00.timingsystem.bluemap.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.justbru00.timingsystem.bluemap.TimingSystemBlueMapPlugin;
import com.justbru00.timingsystem.bluemap.utils.Messager;

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
public class ConfigUpdater {
	private static FileConfiguration config;
	
	public static void updateConfigYml() {
		int currentConfigVersion = ConfigurationManager.CONFIG_VERSION;
		if (config == null) {
			config = TimingSystemBlueMapPlugin.getInstance().getConfig();
		}

		if (config.getInt("config_version") != 0) {
			if (config.getInt("config_version") == currentConfigVersion) {
				// The config version is up to date.
			} else {
				config.set("config_version", currentConfigVersion);
				TimingSystemBlueMapPlugin.getInstance().saveConfig();
			}
		} else {
			// Config value cannot be found.
			config.set("config_version", currentConfigVersion);
			TimingSystemBlueMapPlugin.getInstance().saveConfig();
		}
		
		// Add values if they are missing
		updateConfigYmlBoolean("poi_markers.tracks.spawn_locations.hide_closed_tracks", false);
	}

	@SuppressWarnings("unused")
	private static void updateConfigYmlInteger(String path, int updatedValue) {
		if (!config.isSet(path)) {
			// Path doesn't exist.
			config.set(path, updatedValue);
			Messager.msgConsole("[ConfigUpdater] Added " + path + " to config.yml.");
			TimingSystemBlueMapPlugin.getInstance().saveConfig();
		}
	}

	@SuppressWarnings("unused")
	private static void updateConfigYmlString(String path, String updatedValue) {
		if (!config.isSet(path)) {
			// Path doesn't exist.
			config.set(path, updatedValue);
			Messager.msgConsole("[ConfigUpdater] Added " + path + " to config.yml.");
			TimingSystemBlueMapPlugin.getInstance().saveConfig();
		}
	}

	private static void updateConfigYmlBoolean(String path, boolean updatedValue) {
		if (!config.isSet(path)) {
			// Path doesn't exist.
			config.set(path, updatedValue);
			Messager.msgConsole("[ConfigUpdater] Added " + path + " to config.yml.");
			TimingSystemBlueMapPlugin.getInstance().saveConfig();
		}
	}

	@SuppressWarnings("unused")
	private static void updateConfigYmlStringList(String path, String... updatedValue) {
		if (!config.isSet(path)) {
			// Path doesn't exist.
			List<String> stringList = new ArrayList<String>();
			for (String s : updatedValue) {
				stringList.add(s);
			}

			config.set(path, stringList);
			Messager.msgConsole("[ConfigUpdater] Added " + path + " to config.yml.");
			TimingSystemBlueMapPlugin.getInstance().saveConfig();
		}
	}
}
