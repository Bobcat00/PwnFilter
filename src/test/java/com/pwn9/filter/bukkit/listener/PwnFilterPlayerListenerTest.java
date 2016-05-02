/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test the Bukkit Built-in Chat Filter Listener
 *
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 *
 * Created by Sage905 on 15-09-10.
 */

public class PwnFilterPlayerListenerTest {

    private Player mockPlayer = new MockPlayer();

    private AsyncPlayerChatEvent chatEvent;
    private Configuration testConfig;
    private final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
    private FilterService filterService;
    private PwnFilterPlayerListener playerListener;

    @Before
    public void setUp() throws InvalidConfigurationException {
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        PwnFilterPlugin testPlugin = new MockPlugin();
        playerListener = new PwnFilterPlayerListener(testPlugin);
        filterService = testPlugin.getFilterService();
        filterService.getConfig().setRulesDir(rulesDir);
        testConfig = YamlConfiguration.loadConfiguration(new File(getClass().getResource("/config.yml").getFile()));
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.registerAuthorService(uuid -> new TestAuthor());
        BukkitConfig.loadConfiguration(testConfig, resourcesDir,filterService);
        BukkitConfig.setGlobalMute(false); // To ensure it gets reset between tests.
    }

    @Test
    public void testBasicFunctionWorks() throws Exception {
        String input = "Test Chat Message";
        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("blank.txt");
        playerListener.onPlayerChat(chatEvent);
        assertEquals(chatEvent.getMessage(), input);
    }

    @Test
    public void testExecutesRules() throws Exception {
        String input = "Test replaceme Message";
        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("replace.txt");
        playerListener.onPlayerChat(chatEvent);
        assertEquals(chatEvent.getMessage(), "Test PASS Message");
    }

    @Test
    public void testGlobalMuteCancelsMessage() throws Exception {
        String input = "Test Message";
        BukkitConfig.setGlobalMute(true);
        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("blank.txt");
        playerListener.onPlayerChat(chatEvent);
        assertTrue(chatEvent.isCancelled());
    }

    @Test
    public void testDecolorMessage() throws Exception {
        String input = "Test&4 Message";
        testConfig.set("decolor", true);
        BukkitConfig.loadConfiguration(testConfig, resourcesDir,filterService);

        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("blank.txt");
        playerListener.onPlayerChat(chatEvent);
        assertEquals(chatEvent.getMessage(), "Test Message");
        testConfig.set("decolor", false);
    }

    // https://github.com/Pwn9/PwnFilter/issues/13
    @Test
    public void testLowerMessage() throws Exception {
        String input = "HEY! THIS SHOULD ALL GET LOWERED.";
        BukkitConfig.loadConfiguration(testConfig, resourcesDir,filterService);

        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("actionTests.txt");
        playerListener.onPlayerChat(chatEvent);
        assertTrue(!chatEvent.isCancelled());
        assertEquals(chatEvent.getMessage(), "HEY! this should all get lowered.");
    }

}