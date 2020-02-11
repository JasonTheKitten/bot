package everyos.discord.exobot.util;

import java.util.IllegalFormatException;

public class ChannelIdentifierHelper {
    public static boolean isIdentifier(String string) {
        if (!string.contains("-")||string.startsWith("-")||string.endsWith("-")) return false;
        try {
            Double.valueOf(string.replaceFirst("-", ""));
            return true;
        } catch(IllegalFormatException e) {
            return false;
        }
    }

    public static String getGuildFromIdentifier(String string) {
        return string.substring(0, string.indexOf('-'));
    }

    public static String getChannelFromIdentifier(String string) {
        return string.substring(string.indexOf('-')+1, string.length());
    }
}