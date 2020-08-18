package com.entryrise.coupons.utils;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.entryrise.coupons.Main;

public class Others {

	public static YamlConfiguration getConfig(File f, int version) {
		YamlConfiguration fc = new YamlConfiguration();

		if (!f.exists()) {
			f.getParentFile().mkdirs();
			Main.p.saveResource(f.getName(), false);
		}

		try {
			fc.load(f);

			if (fc.contains("version")) {
				if (fc.getInt("version") != version) {
					Bukkit.getLogger().info(Main.PREFIX + "Updating " + f.getName() + " file!");
					f.renameTo(getOldFile(f));
					Main.p.saveResource(f.getName(), false);
					fc.load(f);

				}
			} else {
				Bukkit.getLogger().info(Main.PREFIX + "Updating " + f.getName() + " file!");
				f.renameTo(getOldFile(f));
				Main.p.saveResource(f.getName(), false);
				fc.load(f);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fc;

	}
	
	private static File getOldFile(File f) {
		return new File(f.getParentFile(), f.getName() + "." + RandomStringUtils.randomAlphanumeric(3) + ".old");
	}
	
}
