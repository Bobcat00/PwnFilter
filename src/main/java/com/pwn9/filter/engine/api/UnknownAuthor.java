/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * This is returned by the engine when an author for a message can not be found.
 *
 * Created by Sage905 on 2016-04-25.
 */
public final class UnknownAuthor implements MessageAuthor {

    private final UUID id;

    public UnknownAuthor(UUID uuid) {
        this.id = uuid;
    }
    @Override
    public Boolean hasPermission(String s) {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void sendMessage(String message) {
        // Silently do nothing, since we don't know who this is.
    }

    @Override
    public void sendMessages(List<String> messages) {
        // Silently do nothing, since we don't know who this is.
    }
}
