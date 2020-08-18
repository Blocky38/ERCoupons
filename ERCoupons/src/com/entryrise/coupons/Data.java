package com.entryrise.coupons;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.entryrise.coupons.mineutils.ItemBuilder;

public class Data {
	private static Executor exec;
	private static File dataf;
	private static FileConfiguration data;

	static {
		Data.exec = Executors.newSingleThreadExecutor();
		dataf = new File(Main.p.getDataFolder(), "data.yml");
		data = (FileConfiguration) new YamlConfiguration();
	}

	public static void Enabler(final boolean reload) {
		try {
			if (!dataf.exists()) {
				dataf.createNewFile();
			}
			data.load(dataf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveData() {
		exec.execute(() -> {
			try {
				data.save(dataf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static boolean setCredits(final UUID u, final long amount) {
		data.set("players." + u.toString() + ".credits", (Object) amount);
		saveData();
		return true;
	}

	public static long getCredits(final UUID u) {
		return data.getLong("players." + u.toString() + ".credits");
	}

	public static long getInCirculation() {
		if (!data.contains("players")) {
			return -1L;
		}
		long circulation = 0L;
		for (final String stg : data.getConfigurationSection("players").getKeys(false)) {
			circulation += data.getLong("players." + stg + ".credits", 0L);
		}
		return circulation;
	}

	public static void createCoupon(final Player p, final int credits) {
		if (p == null) {
			return;
		}

		final UUID u = p.getUniqueId();
		final long available = getCredits(u);

		if (credits > 5000) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou can make a coupon holding a maximum of 5000 credits");
			return;
		}
		if (credits < 50) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou can't make a coupon holding under 50 credits");
			return;
		}
		if (available < credits) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou need another " + (available - credits)
					+ " credits to afford creating this coupon.");
			return;
		}
		if (p.getInventory().firstEmpty() == -1) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou need to make space in your inventory to create a coupon.");
			return;
		}

		final UUID couponid = UUID.randomUUID();

		setCredits(u, available - credits);
		data.set("coupons." + couponid.toString(), (Object) credits);
		saveData();

		ItemStack coupon = new ItemBuilder(Material.ENCHANTED_BOOK).setName("&c&a&b&a&lCredit Coupon")
				.setLore(new String[] { "&f", "&fThis coupon can be redeemed for &4&l" + credits + "&f credits.",
						"&fTo redeem, right click to claim this coupon", "", "&8" + couponid.toString() })
				.build();

		p.getInventory().addItem(coupon);
	}

	public static boolean couponAction(final Player p, final ItemStack coupon, final boolean redeem) {
		final UUID u = p.getUniqueId();

		if (coupon == null) {
			return false;
		}
		if (!coupon.hasItemMeta()) {
			return false;
		}
		final ItemMeta imeta = coupon.getItemMeta();
		if (coupon.getType() != Material.ENCHANTED_BOOK) {
			return false;
		}
		if (!imeta.getDisplayName().equals("§c§a§b§a§lCredit Coupon")) {
			return false;
		}
		if (!imeta.hasLore()) {
			safeAbort(p);
			return true;
		}
		final List<String> players = imeta.getLore();
		if (players.size() != 5) {
			safeAbort(p);
			return true;
		}
		String val = players.get(4);

		if (!val.startsWith("§8")) {
			safeAbort(p);
			return true;
		}

		UUID couponid = UUID.fromString(val = val.replace("§8", ""));
		String loc = "coupons." + couponid.toString();

		long credits = data.getLong(loc, -1);
		PlayerInventory pinv = p.getInventory();
		if (credits == -1L) {
			for (int i = 0; i < pinv.getSize(); ++i) {
				final ItemStack itm = pinv.getItem(i);
				if (itm != null) {
					if (itm.isSimilar(coupon)) {
						pinv.setItem(i, null);
					}
				}
			}
			return true;
		}
		if (!redeem) {
			return true;
		}
		data.set(loc, (Object) null);
		for (int i = 0; i < pinv.getSize(); ++i) {
			ItemStack itm = pinv.getItem(i);
			if (itm != null) {
				if (itm.isSimilar(coupon)) {
					pinv.setItem(i, null);
				}
			}
		}
		long current = getCredits(u);
		setCredits(u, current + credits);
		p.sendMessage("§2§lER§f§lCoupons §e» §fYou have redeemed a coupon worth " + credits + " credits. Current: "
				+ (current + credits));
		return true;
	}

	private static void safeAbort(final Player p) {
		Bukkit.savePlayers();
		Bukkit.getWorlds().forEach(w -> w.save());
		for (int i = 0; i < 50; ++i) {
			Bukkit.getLogger().log(Level.SEVERE,
					p.getUniqueId() + " " + p.getName() + " has managed to partially recreate coupon. Aborting server");
		}
		System.exit(0);
	}
}
