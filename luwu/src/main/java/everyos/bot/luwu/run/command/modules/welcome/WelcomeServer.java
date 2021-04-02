package everyos.bot.luwu.run.command.modules.welcome;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class WelcomeServer extends Server {
	public WelcomeServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<WelcomeServerInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new WelcomeServerInfoImp(doc.getObject());
		});
	}
	
	public Mono<Void> edit(Consumer<WelcomeServerEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			func.accept(new WelcomeServerEditSpecImp(doc.getObject()));
			
			return doc.save();
		});
	}
	
	private class WelcomeServerEditSpecImp implements WelcomeServerEditSpec {
		private DBObject object;

		public WelcomeServerEditSpecImp(DBObject object) {
			this.object = object;
		}

		@Override
		public void setWelcomeMessage(ChannelID output, String message) {
			if (output == null) {
				object.remove("wmsg");
				object.remove("wmsgc");
				return;
			}
			object.set("wmsg", message);
			object.set("wmsgc", output.getLong());
		}

		@Override
		public void setLeaveMessage(ChannelID output, String message) {
			if (output == null) {
				object.remove("lmsg");
				object.remove("lmsgc");
				return;
			}
			object.set("lmsg", message);
			object.set("lmsgc", output.getLong());
		}

		@Override
		public WelcomeServerInfo getInfo() {
			return new WelcomeServerInfoImp(object);
		}
	}
	
	private class WelcomeServerInfoImp implements WelcomeServerInfo {
		private DBObject object;

		public WelcomeServerInfoImp(DBObject object) {
			this.object = object;
		}

		@Override
		public Optional<Tuple<ChannelID, String>> getWelcomeMessage() {
			if (!object.has("wmsgc")) {
				return Optional.empty();
			}
			ChannelID channelID = new ChannelID(getConnection(),
				object.getOrDefaultLong("wmsgc", -1L), getClient().getID());
			String message = object.getOrDefaultString("wmsg", "Edge case: this should not be returned");
			return Optional.of(Tuple.of(channelID, message));
		}

		@Override
		public Optional<Tuple<ChannelID, String>> getLeaveMessage() {
			if (!object.has("lmsgc")) {
				return Optional.empty();
			}
			ChannelID channelID = new ChannelID(getConnection(),
				object.getOrDefaultLong("lmsgc", -1L), getClient().getID());
			String message = object.getOrDefaultString("lmsg", "Edge case: this should not be returned");
			return Optional.of(Tuple.of(channelID, message));
		}
	}
	
	public static WelcomeServerFactory type = new WelcomeServerFactory();
}
