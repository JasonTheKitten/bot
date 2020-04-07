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
            if (token.startsWith("!")) token = token.substring(1, token.length());
        }
        try {
            Snowflake.of(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String eatUserID() {
        String token = eat();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) token = token.substring(1, token.length());
        }
        return token;
    }

    public boolean couldBeChannelID() {
        String token = next();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        try {
            Snowflake.of(token); return true;
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
    
    public boolean couldBeRoleID() {
        String token = next();
        if (token.startsWith("<@&") && token.endsWith(">")) {
            token = token.substring(3, token.length() - 1);
        }
        try {
            Snowflake.of(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String eatRoleID() {
        String token = eat();
        if (token.startsWith("<@&") && token.endsWith(">")) {
            token = token.substring(3, token.length() - 1);
        }
        return token;
    }
    
    public boolean couldBeEmojiID() {
        String token = next();
        if (token.startsWith("<:") && token.endsWith(">")) {
            token = token.substring(token.lastIndexOf(':')+1, token.length() - 1);
        }
        try {
            Snowflake.of(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String eatEmojiID() {
        String token = eat();
        if (token.startsWith("<:") && token.endsWith(">")) {
            token = token.substring(token.lastIndexOf(':')+1, token.length() - 1);
        }
        return token;
    }
    
    public boolean couldBeQuote() {
    	String token = next();
    	boolean foundStart = false;
    	boolean escapeNext = false;
    	if (!token.startsWith("\"")) return false;
    	for (char b: token.toCharArray()) {
    		if (escapeNext) {
    			escapeNext = false;
    			continue;
    		}
    		escapeNext = false;
    		if (b=='\\') escapeNext = true;
    		if (b=='"' && foundStart) return true;
    		if (b=='"' && !foundStart) foundStart = true;
    	}
    	return false;
    }
    
    public String eatQuote() {
    	String token = eat();
    	StringBuilder quote = new StringBuilder();
    	boolean foundStart = false;
    	boolean escapeNext = false;
    	for (char b: token.toCharArray()) {
    		if (escapeNext) {
    			escapeNext = false;
    			continue;
    		}
    		escapeNext = false;
    		if (b=='\\') {
    			escapeNext = true;
    			continue;
    		}
    		if (b=='"' && foundStart) break;
    		if (foundStart) quote.append(b);
    		if (b=='"' && !foundStart) foundStart = true;
    	}
    	return quote.toString();
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
        for (String prefix: prefixes) {
            if (entry.startsWith(prefix)) return StringUtil.sub(entry, prefix.length()).trim();
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