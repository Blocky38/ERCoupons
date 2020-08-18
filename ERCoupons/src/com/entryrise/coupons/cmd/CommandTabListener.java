// 
// Decompiled by Procyon v0.5.36
// 

package com.entryrise.coupons.cmd;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabListener implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias, final String[] args) {
        return new ArrayList<String>();
    }
}
