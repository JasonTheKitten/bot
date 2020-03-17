package everyos.discord.bot.parser;

import javax.annotation.Nonnull;

import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.util.StringUtil;

public class ArgumentParser {
    private String argument;

    public ArgumentParser(String argument) {
        this.argument = argument;
    }

    public String next() {
        return getCommand(argument);
    }

    public String eat() {
        String arg = getCommand(argument);
        argument = getArgument(argument);
        return arg;
    }

    public boolean couldBeUserID() {
        String token = next();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) {
                token = token.substring(1, token.length());
            } else if (token.startsWith("&"))
                token = token.substring(1, token.length());
        }
        try {
            Snowflake.of(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String eatUserID() {
        String token = eat();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) {
                token = token.substring(1, token.length());
            } else if (token.startsWith("&"))
                token = token.substring(1, token.length());
        }
        return token;
    }

    public boolean couldBeChannelID() {
        String token = next();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        try {
            Snowflake.of(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String eatChannelID() {
        String token = eat();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        return token;
    }

    public boolean couldBeID() {
        return couldBeChannelID() || couldBeUserID();
    }

    public boolean isNumerical() {
        try {
            Long.valueOf(next());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public long eatNumerical() {
        return Long.valueOf(eat());
    }

    public boolean isEmpty() {
        return argument.trim().isEmpty();
    }

    public String toString() {
        return argument;
    }

    public static String getIfPrefix(@Nonnull String entry, @Nonnull String[] prefixes) {
        for (int i=0; i<prefixes.length; i++) {
            String prefix = prefixes[i];
            if (entry.startsWith(prefix)) return StringUtil.sub(entry, prefix.length());
        }
        return null;
	}

    public static String getCommand(@Nonnull String content) {
        return StringUtil.split1(content, " ");
    }
    public static String getArgument(@Nonnull String content) {
        return StringUtil.split2(content, " ");
    }
}