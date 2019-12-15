package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;

/**
 * Execute a console command
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionconsole implements Action {
    String command;

    public void init(String s)
    {
        command = s;
    }

    public boolean execute(final FilterState state ) {
        final String cmd = Patterns.replaceCommands(command, state);
        state.addLogMessage("Sending console command: " + cmd);
        Bukkit.getScheduler().runTask(state.plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        });
        return true;
    }
}
