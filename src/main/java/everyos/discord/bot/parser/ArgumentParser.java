package everyos.discord.bot.parser;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.util.StringUtil;
import everyos.storage.database.OtherCase;

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
		if (token.startsWith("<@")&&token.endsWith(">")) {
			token = token.substring(2, token.length()-1);
			if (token.startsWith("!")) {token = token.substring(1, token.length());}
            else if (token.startsWith("&")) token = token.substring(1, token.length());
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
		if (token.startsWith("<@")&&token.endsWith(">")) {
			token = token.substring(2, token.length()-1);
            if (token.startsWith("!")) {token = token.substring(1, token.length());}
            else if (token.startsWith("&")) token = token.substring(1, token.length());
        }
		return token;
	}
	public boolean couldBeChannelID() {
		String token = next();
		if (token.startsWith("<#")&&token.endsWith(">")) token = token.substring(2, token.length()-1);
		try {
			Snowflake.of(token);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	public String eatChannelID() {
		String token = eat();
		if (token.startsWith("<#")&&token.endsWith(">")) token = token.substring(2, token.length()-1);
		return token;
	}
	public boolean couldBeID() {
		return couldBeChannelID()||couldBeUserID();
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
    
    public String toString() { return argument; }
	
    
    private static String getFromPing(@Nonnull String entry) {
        ArgumentParser ap = new ArgumentParser(entry);
        if (ap.couldBeUserID()) {
            String id = ap.eatUserID();
            if (id.equals(Main.clientID)) return ap.toString();
        }
        return null;
    }
    public static boolean isDirectPing(@Nonnull String entry) {
        String res = getFromPing(entry);
        return res!=null&&res.isEmpty();
    }
	public static String getIfPrefix(@Nonnull String entry, @Nonnull String[] prefixes) {
        String fromPing = getFromPing(entry);
        if (fromPing!=null) return fromPing;
		for (int i=0; i<prefixes.length; i++) {
			String prefix = prefixes[i];
			if (entry.startsWith(prefix)) return StringUtil.sub(entry, prefix.length());
		}
		return null;
	}
	public static OtherCase ifPrefix(@Nonnull String entry, @Nonnull String[] prefixes, @Nonnull Consumer<String> func) {
		OtherCase elsedo = new OtherCase();
		String arg = getIfPrefix(entry, prefixes);
		elsedo.complete = arg!=null;
		if (elsedo.complete) func.accept(arg);
		return elsedo;
	}
	public static OtherCase ifPrefix(@Nonnull String entry, @Nonnull String prefixes[], @Nonnull Function<String, Boolean> func) {
		OtherCase elsedo = new OtherCase();
		String arg = getIfPrefix(entry, prefixes);
		elsedo.complete = arg!=null;
		if (elsedo.complete) elsedo.complete = func.apply(arg);
		return elsedo;
	}

    public static String getCommand(String content) {
        return StringUtil.split1(content, " ");
    }
    public static String getArgument(String content) {
        return StringUtil.split2(content, " ");
    }
}
