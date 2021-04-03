package everyos.bot.luwu.run.command.modules.logging;

import java.util.Map;
import java.util.function.Function;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class LogsServer extends Server {
	public LogsServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<LogsServerInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new LogServerInfoImp(doc.getObject());
		});
	}
	
	public Mono<Void> edit(Function<LogsServerEditSpec, Mono<Void>> func) {
		return getGlobalDocument().flatMap(doc->{
			return func.apply(new LogServerEditSpecImp(doc.getObject())).then(doc.save());
		});
	}
	

	private class LogServerInfoImp implements LogsServerInfo {
		private DBObject object;

		public LogServerInfoImp(DBObject object) {
			this.object = object;
		}

		@Override
		public Mono<Channel> getLogChannel() {
			long loggingLong = object.getOrDefaultLong("logging", -1L);
			if (loggingLong == -1L) return Mono.empty();
			return new ChannelID(getConnection(), loggingLong, getClient().getID()).getChannel();
		}
	}
	
	private class LogServerEditSpecImp implements LogsServerEditSpec {
		private DBObject object;

		public LogServerEditSpecImp(DBObject object) {
			this.object = object;
		}

		@Override
		public void setLogChannel(ChannelID logChannelID) {
			object.set("logging", logChannelID.getLong());
		}

		@Override
		public void clear() {
			object.remove("logging");
		}
	}
	
	public static LogsServerFactory type = new LogsServerFactory();
}
