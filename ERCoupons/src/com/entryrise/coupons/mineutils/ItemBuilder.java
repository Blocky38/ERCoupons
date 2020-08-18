package com.entryrise.coupons.mineutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	private ItemStack itm;

	public ItemBuilder(Material mat) {
		this.itm = new ItemStack(mat);
	}

	public ItemBuilder(ItemStack itm) {
		this.itm = itm;
	}

	public ItemBuilder(FileConfiguration config, String location, String... placeholders) {
		if (!config.contains(location)) {
			return;
		}
		
		this.setType(config.getString(location + ".material"));
		this.setName(config.getString(location + ".name"));

		List<String> stgs = config.getStringList(location + ".lore");

		if (stgs != null && !stgs.isEmpty()) {
			this.setLore(stgs.toArray(new String[stgs.size()]));
			setLorePlaceholders(placeholders);
		}

		if (config.getBoolean(location + ".shiny")) {
			this.setShiny();
		}

		setNamePlaceholders(placeholders);

	}

	public ItemBuilder setType(String material) {
		if (material == null) {
			material = "BARRIER";
		}
		ItemMeta imeta = (itm == null) ? null : itm.getItemMeta().clone();

		String displayname = null;
		List<String> lore = null;
		Set<ItemFlag> flags = null;
		Map<Enchantment, Integer> enchants = null;

		if (imeta != null) {
			displayname = imeta.hasDisplayName() ? imeta.getDisplayName() : null;
			lore = imeta.hasLore() ? imeta.getLore() : null;
			enchants = imeta.hasEnchants() ? imeta.getEnchants() : null;
			flags = imeta.getItemFlags();
		}

//		Bukkit.getPlayer("Stefytorus").getInventory().addItem(itm);

		String[] split = material.split(":");
		
		if (material.startsWith("head:")) {
			itm = HeadUtils.getPlayerSkull(material.replace("head:", ""));
		} else if (material.startsWith("urlhead:")) {
			itm = HeadUtils.getSkull(material.replace("urlhead:", ""));
		} else if (split.length == 2) {
			Material mat = Material.getMaterial(split[0]);
			if (mat == null) {
				mat = Material.getMaterial("BARRIER");
			}
			itm = new ItemStack(mat, 1, Short.valueOf(split[1]));
		} else {
			Material mat = Material.getMaterial(material);
			if (mat == null) {
				mat = Material.getMaterial("BARRIER");
			}
			itm = new ItemStack(mat);
		}

		imeta = itm.getItemMeta();
		if (lore != null) {
			imeta.setLore(lore);
		}
		if (displayname != null) {
			imeta.setDisplayName(displayname);
		}
		if (enchants != null) {
			for (Enchantment ench : enchants.keySet()) {
				imeta.addEnchant(ench, enchants.get(ench), true);
			}
		}
		if (flags != null) {
			imeta.addItemFlags(flags.toArray(new ItemFlag[flags.size()]));
		}
		itm.setItemMeta(imeta);

		return this;
	}

	public ItemBuilder setName(String name) {
		ItemMeta imeta = itm.getItemMeta();
		imeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		itm.setItemMeta(imeta);
		return this;
	}

	public ItemBuilder setShiny() {
		ItemMeta imeta = itm.getItemMeta();
		imeta.addEnchant(Enchantment.DURABILITY, 10, true);
		imeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itm.setItemMeta(imeta);
		return this;
	}

	public ItemBuilder setLore(String... lore) {
		List<String> solved = new ArrayList<String>();
		for (String stg : lore) {
			solved.add(ChatColor.translateAlternateColorCodes('&', stg));
		}
		ItemMeta imeta = itm.getItemMeta();
		imeta.setLore(solved);
		itm.setItemMeta(imeta);
		return this;
	}

	public ItemBuilder setLorePlaceholders(String... placeholders) {
		ItemMeta imeta = itm.getItemMeta();
		List<String> lore = imeta.getLore();

		for (int i = 0; i < lore.size(); i++) {
			String val = lore.get(i);
			for (int j = 0; j < placeholders.length; j += 2) {
				val = val.replace(placeholders[j], placeholders[j + 1]);
			}
			lore.set(i, val);
		}
		imeta.setLore(lore);
		itm.setItemMeta(imeta);
		return this;
	}

	public ItemBuilder setLorePlaceholders(String placeholder, List<String> placement) {
		ItemMeta imeta = itm.getItemMeta();
		List<String> lore = imeta.getLore();
		List<String> newlore = new ArrayList<String>();
		for (int i = 0; i < lore.size(); i++) {
			String val = lore.get(i);
			if (val.contains(placeholder)) {
				val = val.replace(placeholder, "");
				newlore.add(val);
				for (String stg : placement) {
					newlore.add(stg);
				}
				continue;
			}
			newlore.add(val);
		}
		imeta.setLore(newlore);
		itm.setItemMeta(imeta);
		return this;
	}

	public ItemBuilder setNamePlaceholders(String... placeholders) {
		ItemMeta imeta = itm.getItemMeta();
		String name = imeta.getDisplayName();

		for (int j = 0; j < placeholders.length; j += 2) {
			name = name.replace(placeholders[j], placeholders[j + 1]);
		}
		imeta.setDisplayName(name);
		itm.setItemMeta(imeta);
		return this;
	}

	public ItemStack build() {
		return itm;
	}

}
