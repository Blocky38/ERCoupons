// 
// Decompiled by Procyon v0.5.36
// 

package com.entryrise.coupons.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.entryrise.coupons.Data;
import com.entryrise.coupons.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.chat.BaseComponent;

public class CSUtils {
	private static String url = "https://api.craftingstore.net/v7/gift-cards";
	
	public static void redeemStore(final Player p, final long credits) {
		if (p == null) {
			return;
		}
		if (credits < 1L) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou need to specify the amount of credits to redeem your credits.");
			return;
		}
		if (credits % 100L != 0L) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou can only redeem multiples of 100 credits.");
			return;
		}
		final UUID u = p.getUniqueId();
		final long available = Data.getCredits(u);
		final long rem = available - credits;
		if (rem < 0L) {
			p.sendMessage("§2§lER§f§lCoupons §e» §fYou need " + -rem
					+ " more credits to be able to create this store giftcard");
			return;
		}
		Data.setCredits(u, rem);
		final long dollars = credits / 100L;
		p.sendMessage("§2§lER§f§lCoupons §e» §fWe're currently contacting the store and creating your coupon...");
		Bukkit.getScheduler().runTaskAsynchronously(Main.p, () -> CSUtils.giveCoupon(p, dollars));
	}

	public static void giveCoupon(final Player p, final long dollars) {
		final UUID transactionid = UUID.randomUUID();
		Bukkit.getLogger().info("STORE TRANSACTION " + transactionid.toString() + " with " + p.getName() + " STARTED!");
		try {
			final URL u = new URL(CSUtils.url);
			
			final HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
			final String urlParameters = "{\"amount\": \"" + dollars + "\"}";
			final byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("token", Main.config.getString("api-key"));
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", new StringBuilder().append(postData.length).toString());
			conn.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);

			final String stg = (conn.getResponseCode() == 200) ? getInputStreamString(conn.getInputStream())
					: getInputStreamString(conn.getErrorStream());

			final JsonParser parser = new JsonParser();
			final JsonObject o = parser.parse(stg).getAsJsonObject();
			final String code = ((JsonObject) o.get("data")).get("code").getAsString();

			p.sendMessage("§2§lER§f§lCoupons §e» §fGIFT-CARD Succesfully printed below.");
			p.spigot().sendMessage((BaseComponent) Chat.genHoverAndSuggestTextComponent(
					"§2§lER§f§lCoupons §e» §f§c" + code, "Click, CTRL+A, CTRL+C, and paste with CTRL+V", code));
		} catch (Exception e) {
			p.sendMessage(
					"§2§lER§f§lCoupons §e» §fSomething went wrong with creating your store coupon. Urgently contact staff. "
							+ transactionid.toString());
			Bukkit.getLogger()
					.warning("STORE TRANSACTION " + transactionid.toString() + " with " + p.getName() + " WENT WRONG!");
			e.printStackTrace();
			return;
		}
		Bukkit.getLogger()
				.info("STORE TRANSACTION " + transactionid.toString() + " with " + p.getName() + " WENT WELL!");
	}

	public static String getInputStreamString(InputStream is) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception e) {
			return null;
		}
	}
}
