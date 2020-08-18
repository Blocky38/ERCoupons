// 
// Decompiled by Procyon v0.5.36
// 

package com.entryrise.coupons.listeners;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.entryrise.coupons.Data;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class CouponListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCouponInteract(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final ItemStack itm = e.getItem();
        if (e.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Data.couponAction(p, itm, true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGUIClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        final ItemStack itm = e.getCurrentItem();
        Data.couponAction(p, itm, false);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGUIDrag(final InventoryDragEvent e) {
        final Player p = (Player)e.getWhoClicked();
        final ItemStack itm = e.getOldCursor();
        if (Data.couponAction(p, itm, false)) {
            e.setCancelled(true);
            e.setCursor((ItemStack)null);
        }
    }
}
