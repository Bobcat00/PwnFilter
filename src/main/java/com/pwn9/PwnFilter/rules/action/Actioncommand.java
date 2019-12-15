package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;

/**
 * Execute a command as a player.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actioncommand implements Action {
    String command;

    public void init(String s)
    {
        command = s;
    }

    public boolean execute(final FilterState state ) {
        state.cancel = true;
        final String cmd;
        if (!command.isEmpty()) {
            cmd = Patterns.replaceCommands(command, state);
            state.addLogMessage("Helped " + state.playerName + " execute command: " + cmd);
        } else {
            cmd = state.message.getColoredString();
        }
        state.addLogMessage("Helped " + state.playerName + " execute command: " + cmd);
        Bukkit.getScheduler().runTask(state.plugin, new Runnable() {
            @Override
            public void run() {
                state.player.chat("/" + cmd);
            }
        });

        return true;
    }
}
