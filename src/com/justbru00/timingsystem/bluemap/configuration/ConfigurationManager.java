package com.justbru00.timingsystem.bluemap.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import com.justbru00.timingsystem.bluemap.TimingSystemBlueMapPlugin;

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
public class ConfigurationManager {

	public static final int CONFIG_VERSION = 1;	
	private static FileConfiguration config = null;
	
	/**
	 * Checks the config version number vs the one this version has.
	 * @return True if it needs updated, false if it doesn't.
	 */
	public static boolean doesConfigYmlNeedUpdated() {
		if (config == null) {
			config = TimingSystemBlueMapPlugin.getInstance().getConfig();
		}
		
		return config.getInt("config_version") < CONFIG_VERSION;	
	}
	
	/**
	 * Adds any new config values to the config.yml file if the config_version value doesn't match the value of this version of EpicRename.
	 * These must be set manually in the {@link ConfigUpdater}.
	 * Doesn't touch old values.
	 */
	public static void updateConfigYml() {
		ConfigUpdater.updateConfigYml();
	}
	
}
