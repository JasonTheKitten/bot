package everyos.discord.luwu.parser;

import javax.annotation.Nonnull;

import discord4j.common.util.Snowflake;
import everyos.discord.luwu.util.StringUtil;

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

    public long eatUserID() {
        String token = eat();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) token = token.substring(1, token.length());
        }
        return Long.valueOf(token);
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

    public long eatChannelID() {
        String token = eat();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        return Long.valueOf(token);
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

    public long eatRoleID() {
        String token = eat();
        if (token.startsWith("<@&") && token.endsWith(">")) {
            token = token.substring(3, token.length() - 1);
        }
        return Long.valueOf(token);
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
    	String token = argument;
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
    	StringBuilder quote = new StringBuilder();
    	boolean foundStart = false;
    	boolean escapeNext = false;
    	while (true) {
    		char b = argument.substring(0, 1).charAt(0);
    		argument = argument.length()>1?argument.substring(1):"";
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
    	argument = argument.trim();
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