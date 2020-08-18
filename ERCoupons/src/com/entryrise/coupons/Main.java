package com.entryrise.coupons;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.entryrise.coupons.cmd.CommandListener;
import com.entryrise.coupons.cmd.CommandTabListener;
import com.entryrise.coupons.listeners.ListenersMain;
import com.entryrise.coupons.utils.Others;

public class Main extends JavaPlugin implements Listener
{
	public static String USER = "%%__USER__%%";
	public static final String PREFIX = "§2§lER§f§lCoupons §e» §f";

	public static JavaPlugin p;

	private static File file;
	public static FileConfiguration config = new YamlConfiguration();

	@Override
	public void onEnable() {
		p = this;

		file = new File(getDataFolder(), "server.yml");
		config = Others.getConfig(file, 1);

		Bukkit.getLogger().info(PREFIX + "Enabling Systems:");
		EnableClasses(false);

		getServer().getPluginManager().registerEvents(this, this);
		getCommand("ercoupons").setExecutor(new CommandListener());
		getCommand("ercoupons").setTabCompleter(new CommandTabListener());
	}
    
    private static void EnableClasses(final boolean reload) {
        Data.Enabler(reload);
        ListenersMain.Enabler(reload);
    }
    
    public static void ReloadPlugin(final CommandSender s) {
        Main.config = Others.getConfig(Main.file, 1);
        
        Bukkit.getLogger().info("§2§lER§f§lCoupons §e» §fReloading Systems:");
        EnableClasses(true);
        
        s.sendMessage("§2§lER§f§lCoupons §e» §fReloaded the config successfully.");
    }
    
    public void onDisable() {
    }
}
