package com.pwn9.PwnFilter.util;

import com.pwn9.PwnFilter.FilterState;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Some helpful utility methods.
 */
public class Patterns {
    /**
     * Class Utility Methods
     */
    static Logger logger = Logger.getLogger("Minecraft.PwnFilter");

    public static java.util.regex.Pattern compilePattern(String re) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
            logger.fine("Successfully compiled regex: " + re);
            return pattern;
        }
        catch (PatternSyntaxException e) {
            logger.warning("Failed to compile regex: " + re);
            logger.warning(e.getMessage());
        }
        catch (Exception e) {
            logger.severe("Unexpected error while compiling expression '" + re + "'");
            e.printStackTrace();
        }
        return pattern;
    }

    public static String replaceCommands(String cmd, FilterState state) {
        cmd = cmd.replaceAll("&world", (state.playerWorldName != null)?state.playerWorldName:"NoWorld").
                replaceAll("&player", (state.playerName != null)?state.playerName:"NoPlayer!").
                replaceAll("&string", state.message.getColoredString()).
                replaceAll("&rawstring", state.getOriginalMessage().getColoredString()).
                replaceAll("&event", (state.eventType != null)?state.eventType.toString():"No Event!");
        return cmd;
    }
}
