/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

/**
 * Created by Sage905 on 2016-04-18.
 */
public class MockBook implements BookMeta {

    private String title, author;
    private LinkedList<String> pages;

    public MockBook(String title, String author) {
        this.title = title;
        this.author = author;
        this.pages = new LinkedList<>();
    }

    public MockBook(String title, String author, List<String> pages) {
        this.title = title;
        this.author = author;
        this.pages = new LinkedList<>(pages);
    }

    @Override
    public boolean hasTitle() {
        return true;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean setTitle(String title) {
        this.title = title;
        return true;
    }

    @Override
    public boolean hasAuthor() {
        return author != null;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean hasPages() {
        return !pages.isEmpty();
    }

    @Override
    public String getPage(int page) {
        return pages.get(page);
    }

    @Override
    public void setPage(int page, String data) {
        pages.set(page, data);
    }

    @Override
    public List<String> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Override
    public void setPages(List<String> pages) {
        this.pages = new LinkedList<>(pages);
    }

    @Override
    public void setPages(String... pages) {
        this.pages = new LinkedList<>(Arrays.asList(pages));
    }

    @Override
    public void addPage(String... pages) {
        Collections.addAll(this.pages, pages);
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public BookMeta clone() {
        return new MockBook(title, author, pages);
    }

    @Override
    public boolean hasDisplayName() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String name) {

    }

    @Override
    public boolean hasLore() {
        return false;
    }

    @Override
    public List<String> getLore() {
        return null;
    }

    @Override
    public void setLore(List<String> lore) {

    }

    @Override
    public boolean hasEnchants() {
        return false;
    }

    @Override
    public boolean hasEnchant(Enchantment ench) {
        return false;
    }

    @Override
    public int getEnchantLevel(Enchantment ench) {
        return 0;
    }

    @Override
    public Map<Enchantment, Integer> getEnchants() {
        return null;
    }

    @Override
    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        return false;
    }

    @Override
    public boolean removeEnchant(Enchantment ench) {
        return false;
    }

    @Override
    public boolean hasConflictingEnchant(Enchantment ench) {
        return false;
    }

    @Override
    public void addItemFlags(ItemFlag... itemFlags) {

    }

    @Override
    public void removeItemFlags(ItemFlag... itemFlags) {

    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return null;
    }

    @Override
    public boolean hasItemFlag(ItemFlag flag) {
        return false;
    }

    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
