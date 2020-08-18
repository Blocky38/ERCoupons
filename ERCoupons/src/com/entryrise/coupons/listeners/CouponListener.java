// 
// Decompiled by Procyon v0.5.36
// 

package com.entryrise.coupons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.entryrise.coupons.Data;

public class CouponListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCouponInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final ItemStack itm = e.getItem();
		if (e.getAction().toString().contains("RIGHT_CLICK")) {
			e.setCancelled(true);
			return;
		}
		Data.couponAction(p, itm, true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGUIClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		final ItemStack itm = e.getCurrentItem();
		Data.couponAction(p, itm, false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGUIDrag(InventoryDragEvent e) {
		final Player p = (Player) e.getWhoClicked();
		final ItemStack itm = e.getOldCursor();
		if (Data.couponAction(p, itm, false)) {
			e.setCancelled(true);
			e.setCursor((ItemStack) null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGUIDrag(PlayerDropItemEvent e) {
		if (Data.couponAction(e.getPlayer(), e.getItemDrop().getItemStack(), false)) {
			e.getItemDrop().remove();
		}
	}
}
