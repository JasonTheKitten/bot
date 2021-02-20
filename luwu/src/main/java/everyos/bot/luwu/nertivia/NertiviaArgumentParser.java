package everyos.bot.luwu.nertivia;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.MessageID;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.entity.ServerID;
import everyos.bot.luwu.core.entity.UserID;

public class NertiviaArgumentParser extends ArgumentParser {
	private Connection connection;
	
	public NertiviaArgumentParser(Connection connection, String argument) {
		super(argument);
		this.connection = connection;
	}

	@Override public boolean couldBeUserID() {
		String token = peek();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) token = token.substring(1, token.length());
        }
        try {
        	Long.valueOf(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
	}
	@Override public UserID eatUserID() {
		String token = eat();
        if (token.startsWith("<@") && token.endsWith(">")) {
            token = token.substring(2, token.length() - 1);
            if (token.startsWith("!")) token = token.substring(1, token.length());
        }
        long uid = Long.valueOf(token);
        return new UserID(connection, uid);
	}

	@Override public boolean couldBeChannelID() {
		String token = peek();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        try {
        	Long.valueOf(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
	}
	@Override public ChannelID eatChannelID() {
		String token = eat();
        if (token.startsWith("<#") && token.endsWith(">"))
            token = token.substring(2, token.length() - 1);
        return new ChannelID(connection, Long.valueOf(token), connection.getClient().getID());
	}

	@Override public boolean couldBeGuildID() {
		return isNumerical();
	}
	@Override public ServerID eatGuildID() {
		return new ServerID(connection, Long.valueOf(eat()));
	}

	@Override public boolean couldBeRoleID() {
		String token = peek();
        if (token.startsWith("<@&") && token.endsWith(">")) {
            token = token.substring(3, token.length() - 1);
        }
        try {
        	Long.valueOf(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
	}
	@Override public RoleID eatRoleID() {
		String token = eat();
        if (token.startsWith("<@&") && token.endsWith(">")) {
            token = token.substring(3, token.length() - 1);
        }
        return new RoleID(connection, Long.valueOf(token));
	}

	@Override public boolean couldBeEmojiID() {
		String token = peek();
        if (token.startsWith("<:") && token.endsWith(">")) {
            token = token.substring(token.lastIndexOf(':')+1, token.length() - 1);
        }
        try {
        	Long.valueOf(token); return true;
        } catch (NumberFormatException e) {
            return false;
        }
	}
	@Override public EmojiID eatEmojiID() {
		String token = eat();
        if (token.startsWith("<:") && token.endsWith(">")) {
            token = token.substring(token.lastIndexOf(':')+1, token.length() - 1);
        }
        try {
        	long rid = Long.valueOf(token);
        	return EmojiID.of(rid);
        } catch(NumberFormatException e) {
        	String name = token;
        	return new EmojiID() {
				@Override
				public Optional<String> getName() {
					return Optional.of(name);
				}

				@Override
				public Optional<Long> getID() {
					return Optional.empty();
				}
        	};
        }
	}

	@Override
	public boolean couldBeMessageID() {
		return isNumerical();
	}

	@Override
	public MessageID eatMessageID(ChannelID channel) {
		String token = eat();
		return new MessageID(channel, Long.valueOf(token));
	}
	
	@Override
	public boolean couldBeURL() {
		String token = peek();
        if (token.startsWith("<") && token.endsWith(">")) {
            token = token.substring(1, token.length() - 1);
        }
        try {
        	new URL(token); return true;
        } catch (MalformedURLException e) {
            return false;
		}
	}

	@Override
	public String eatURL() {
		String token = eat();
        if (token.startsWith("<") && token.endsWith(">")) {
            token = token.substring(1, token.length() - 1);
        }
        
        return token;
	}
}