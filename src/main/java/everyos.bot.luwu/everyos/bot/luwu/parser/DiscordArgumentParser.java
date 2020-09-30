package everyos.bot.luwu.parser;

import discord4j.common.util.Snowflake;

public class DiscordArgumentParser extends ArgumentParser {
	public DiscordArgumentParser(String argument) {
		super(argument);
	}

	@Override public boolean couldBeUserID() {
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
	@Override public long eatUserID() {
		String token = eat();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) token = token.substring(1, token.length());
        }
        return Long.valueOf(token);
	}

	@Override public boolean couldBeChannelID() {
		String token = next();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        try {
            Snowflake.of(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
	}
	@Override public long eatChannelID() {
		String token = eat();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        return Long.valueOf(token);
	}

	@Override public boolean couldBeGuildID() {
		return isNumerical();
	}
	@Override public long eatGuildID() {
		return Long.valueOf(eat());
	}

	@Override public boolean couldBeRoleID() {
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
	@Override public long eatRoleID() {
		String token = eat();
        if (token.startsWith("<@&") && token.endsWith(">")) {
            token = token.substring(3, token.length() - 1);
        }
        return Long.valueOf(token);
	}

	@Override public boolean couldBeEmojiID() {
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
	@Override public String eatEmojiID() {
		String token = eat();
        if (token.startsWith("<:") && token.endsWith(">")) {
            token = token.substring(token.lastIndexOf(':')+1, token.length() - 1);
        }
        return token;
	}

}
