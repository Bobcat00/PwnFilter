/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.api.UnknownAuthor;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.minecraft.util.ColoredString;
import com.pwn9.filter.util.SimpleString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;

/**
 * Listen for Chat events and apply the filter.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class PwnFilterPlayerListener extends BaseListener {
    private final PwnFilterPlugin plugin;
    /**
     * <p>getShortName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getShortName() {
        return "CHAT";
    }

	/**
	 * <p>Constructor for PwnFilterPlayerListener.</p>
	 *
     * @param plugin
     */
	public PwnFilterPlayerListener(PwnFilterPlugin plugin){
        super(plugin.getFilterService());
        this.plugin = plugin;
    }

    /**
     * <p>onPlayerChat.</p>
     *
     * @param event a {@link org.bukkit.event.player.AsyncPlayerChatEvent} object.
     */
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) return;

        MessageAuthor minecraftPlayer = plugin.getFilterService().getAuthor((event.getPlayer().getUniqueId()));

        // This should never happen.  Log it, if it does.
        if (minecraftPlayer instanceof UnknownAuthor) {
            plugin.getLogger().info("Filtering Aborted. Unable to lookup player in Chat Event.  PlayerUUID: "
                    + event.getPlayer().getUniqueId());
            plugin.getLogger().info("Message: " + event.getMessage());
            plugin.getLogger().info("AuthorServices: " + filterService.getAuthorServices());
            plugin.getLogger().info("Bukkit player online: " + event.getPlayer().isOnline());
            return;
        }

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (minecraftPlayer.hasPermission("pwnfilter.bypass.chat")) return;

        String message = event.getMessage();

        // Global mute
        if ((BukkitConfig.globalMute()) && (!minecraftPlayer.hasPermission("pwnfilter.bypass.mute"))) {
            event.setCancelled(true);
            return; // No point in continuing.
        }

        if (BukkitConfig.spamfilterEnabled() && !minecraftPlayer.hasPermission("pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (PwnFilterBukkitPlugin.lastMessage.containsKey(minecraftPlayer.getId()) && PwnFilterBukkitPlugin.lastMessage.get(minecraftPlayer.getId()).equals(message)) {
                event.setCancelled(true);
                minecraftPlayer.sendMessage(ChatColor.DARK_RED + "[PwnFilter]" + ChatColor.RED + " Repeated command blocked by spam filter.");

                return;
            }
            PwnFilterBukkitPlugin.lastMessage.put(minecraftPlayer.getId(), message);

        }

        FilterContext state = new FilterContext(new ColoredString(message), minecraftPlayer, this);

        // Global decolor
        if ((BukkitConfig.decolor()) && !(minecraftPlayer.hasPermission("pwnfilter.color"))) {
            // We are changing the state of the message.  Let's do that before any rules processing.
            state.setModifiedMessage(new SimpleString(state.getModifiedMessage().toString()));
        }

        // Take the message from the ChatEvent and send it through the filter.
        plugin.getLogger().finer("Applying '" + ruleChain.getConfigName() + "' to message: " + state.getModifiedMessage());
        ruleChain.execute(state, filterService);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.getModifiedMessage().getRaw());
        }
        if (state.isCancelled()) event.setCancelled(true);
    }

    /**
     * {@inheritDoc}
     *
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override
    public void activate() {

        if (isActive()) return;

        try {

            ruleChain = getCompiledChain(filterService.getConfig().getRuleFile("chat.txt"));

            PluginManager pm = Bukkit.getServer().getPluginManager();

            /* Hook up the Listener for PlayerChat events */
            pm.registerEvent(AsyncPlayerChatEvent.class, this, BukkitConfig.getChatpriority(),
                    (l, e) -> onPlayerChat((AsyncPlayerChatEvent) e), PwnFilterBukkitPlugin.getInstance());

            plugin.getLogger().info("Activated PlayerListener with Priority Setting: " + BukkitConfig.getChatpriority().toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

            setActive();
        } catch (InvalidChainException e) {
            plugin.getLogger().severe("Unable to activate PlayerListener.  Error: " + e.getMessage());
            setInactive();
        }
    }

}


