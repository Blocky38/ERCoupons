package com.entryrise.coupons.listeners;

import org.bukkit.Bukkit;

import com.entryrise.coupons.Main;

public class ListenersMain {

	public static void Enabler(boolean reload) {
		if (!reload) {
			registerEvents();
		}
		Bukkit.getLogger().info("    §e[§a✔§e] §fEvent-Listeners loaded");
	}
	
	private static void registerEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(new CouponListener(), Main.p);
	}
	
}
