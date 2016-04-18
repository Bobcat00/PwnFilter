/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.bukkit.MockBook;
import com.pwn9.filter.bukkit.MockServer;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test the Bukkit Built-in Chat Filter Listener
 *
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 *
 * Created by Sage905 on 15-09-10.
 */

public class PwnFilterBookListenerTest {

    private Player mockPlayer = new MockPlayer();

    private PlayerEditBookEvent event;
    private Configuration testConfig;
    private final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
    private PwnFilterPlugin testPlugin;
    private FilterService filterService;
    private PwnFilterBookListener bookListener;
    final String[] testPages = new String[]{
            "This is the first page",
            "This is the second page",
            "This is the third page",
            "This is the final page"};

    @Before
    public void setUp() throws InvalidConfigurationException {
        if (Bukkit.getServer() == null ) {
            Bukkit.setServer(new MockServer());
        }
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        testPlugin = new MockPlugin();
        bookListener = new PwnFilterBookListener(testPlugin);
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
        final BookMeta bookMeta = new MockBook("TestTitle", mockPlayer.getName());
        bookMeta.setPages(testPages);

        event = new PlayerEditBookEvent(mockPlayer, 1, new MockBook("", ""), bookMeta, false);

        bookListener.loadRuleChain("blank.txt");
        bookListener.onBookEdit(event);

        for (int i=0 ; i < bookMeta.getPageCount()-1 ; i++) {
            assertEquals(testPages[i],event.getNewBookMeta().getPage(i));
        }

    }

    @Test
    public void testBasicReplacementWorks() throws Exception {
        final BookMeta oldBookMeta = new MockBook("TestTitle", mockPlayer.getName());
        oldBookMeta.setPages(testPages);
        String testPage = "This test should replaceme";
        oldBookMeta.addPage(testPage);

        event = new PlayerEditBookEvent(mockPlayer, 1, new MockBook("", ""), oldBookMeta, false);

        bookListener.loadRuleChain("replace.txt");
        bookListener.onBookEdit(event);

        int pageCount = oldBookMeta.getPageCount();
        BookMeta newBookMeta = event.getNewBookMeta();
        for (int i=0 ; i < pageCount-2 ; i++) {
            assertEquals(testPages[i],event.getNewBookMeta().getPage(i));
        }
        assertEquals("This test should PASS", newBookMeta.getPage(pageCount - 1));

    }


}