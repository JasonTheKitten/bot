package everyos.bot.luwu.run.command.modules.tickets.server;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class TicketServer extends Server {
	
	public TicketServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<TicketServerInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new TicketServerInfoImp(doc.getObject());
		});
	}
	
	public Mono<Void> edit(Consumer<TicketServerEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			func.accept(new TicketServerEditSpecImp(doc.getObject()));
			
			return doc.save();
		});
	}
	
	private class TicketServerInfoImp implements TicketServerInfo {
		private DBObject object;

		public TicketServerInfoImp(DBObject object) {
			this.object = object;
		}

		@Override
		public boolean getEnabled() {
			return object.getOrDefaultBoolean("tike", false);
		}
	}
	
	private class TicketServerEditSpecImp implements TicketServerEditSpec {
		private DBObject object;

		public TicketServerEditSpecImp(DBObject object) {
			this.object = object;
		}

		@Override
		public void setEnabled(boolean enabled) {
			object.set("tike", enabled);
		}
	}

	public static final TicketServerFactory type = new TicketServerFactory();
	
}
