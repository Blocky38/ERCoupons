// 
// Decompiled by Procyon v0.5.36
// 

package com.entryrise.coupons.cmd;

import org.bukkit.Bukkit;
import java.util.UUID;
import com.entryrise.coupons.utils.CSUtils;
import com.entryrise.coupons.utils.MathUtils;
import com.entryrise.coupons.Data;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class CommandListener implements CommandExecutor {
	@SuppressWarnings({ "deprecation"})
	public boolean onCommand(CommandSender sender, final Command cmd, final String label, final String[] args) {
		final Player p = (sender instanceof Player) ? (Player) sender : null;
		if (args.length == 0) {
			if (p == null) {
				sender.sendMessage("§2§lER§f§lCoupons §e» §f");
				return true;
			}
			sender.sendMessage("§2§lER§f§lCoupons §e» §fYou have " + Data.getCredits(p.getUniqueId())
					+ " credits in your account.");
			return true;
		} else {
			if (args.length == 1) {
				final int count = MathUtils.isInt(args[0]) ? Integer.valueOf(args[0]) : -1;
				Data.createCoupon(p, count);
				return true;
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("store")) {
					final int count = MathUtils.isInt(args[1]) ? Integer.valueOf(args[1]) : -1;
					CSUtils.redeemStore(p, (long) count);
					return true;
				}
			} else if (args.length == 3 && sender.isOp()) {
				if (args[0].equalsIgnoreCase("givecredits")) {
					UUID u = null;
					try {
						u = UUID.fromString(args[1]);
					} catch (Exception e) {
						u = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					}
					final int amount = MathUtils.isInt(args[2]) ? Integer.valueOf(args[2]) : 0;
					if (amount == 0) {
						sender.sendMessage("§2§lER§f§lCoupons §e» §fMUST BE NEGATIVE OR POSITIVE NUMBER.");
						return true;
					}
					Data.setCredits(u, Data.getCredits(u) + amount);
					return true;
				} else if (args[0].equalsIgnoreCase("givedollars")) {
					UUID u = null;
					try {
						u = UUID.fromString(args[1]);
					} catch (Exception e) {
						u = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					}
					final double amount2 = MathUtils.isDouble(args[2]) ? Double.valueOf(args[2]) : -1.0;
					if (amount2 == -1.0) {
						sender.sendMessage("§2§lER§f§lCoupons §e» §fMUST BE NEGATIVE OR POSITIVE NUMBER.");
						return true;
					}
					Data.setCredits(u, Data.getCredits(u) + (long) (amount2 * 100.0));
					return true;
				}
			}
			return false;
		}
	}
}
