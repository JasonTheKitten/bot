package everyos.bot.luwu.run.command.modules.tickets.channel;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class TicketChannel extends Channel {
	
	public TicketChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}
	
	public Mono<TicketChannelInfo> getInfo() {
		return getGlobalDocument().map(doc -> {
			return new TicketChannelInfoImp(doc.getObject());
		});
	}
	
	public Mono<Void> edit(Consumer<TicketChannelEditSpec> func) {
		return getGlobalDocument().flatMap(doc -> {
			func.accept(new TicketChannelEditSpecImp(doc.getObject()));
			
			return doc.save();
		});
	}
	
	private class TicketChannelInfoImp implements TicketChannelInfo {
		//private DBObject object;

		public TicketChannelInfoImp(DBObject object) {
			//this.object = object;
		}
	}
	
	private class TicketChannelEditSpecImp implements TicketChannelEditSpec {
		private DBObject object;

		public TicketChannelEditSpecImp(DBObject object) {
			this.object = object;
		}

		@Override
		public void configure() {
			object.set("type", "ticket");
		}
	}

	public static final TicketChannelFactory type = new TicketChannelFactory();
	
}
