package everyos.bot.luwu.run.command.modules.starboard;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.entity.MessageID;
import reactor.core.publisher.Mono;

public class StarboardMessage extends Message {
	protected StarboardMessage(Connection connection, ChatMessage message, Map<String, DBDocument> documents) {
		super(connection, message, documents);
	}
	
	public static StarboardMessageFactory type = new StarboardMessageFactory();

	public Mono<StarboardMessageInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new StarboardMessageInfoImp(doc.getObject(), this);
		});
	}
	
	public Mono<Void> editInfo(Consumer<StarboardMessageEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			func.accept(new StarboardMessageEditSpecImp(doc.getObject()));
			
			return doc.save();
		});
	}
	
	private class StarboardMessageInfoImp implements StarboardMessageInfo {
		private final DBObject object;
		private final StarboardMessage message;

		public StarboardMessageInfoImp(DBObject object, StarboardMessage message) {
			this.object = object;
			this.message = message;
		}

		@Override
		public boolean isStarboardMessage() {
			return object.has("ogmid");
		}

		@Override
		public Mono<StarboardMessage> getStarboardMessage() {
			if (isStarboardMessage()) return Mono.just(message);
			ChannelID cid = new ChannelID(getConnection(), object.getOrDefaultLong("sbcid", -1L), getClient().getID());
			return new MessageID(cid, object.getOrDefaultLong("sbmid", -1L))
				.getMessage()
				.flatMap(c->c.as(StarboardMessage.type));
		}

		@Override
		public Mono<StarboardMessage> getOriginalMessage() {
			if (hasStarboardMessage()) return Mono.just(message);
			ChannelID cid = new ChannelID(getConnection(), object.getOrDefaultLong("ogcid", -1L), getClient().getID());
			return new MessageID(cid, object.getOrDefaultLong("ogmid", -1L))
				.getMessage()
				.flatMap(c->c.as(StarboardMessage.type));
		}

		@Override
		public boolean hasStarboardMessage() {
			return object.has("sbmid");
		}

		@Override
		public Mono<StarboardServer> getStarboardServer() {
			return message.getChannel()
				.flatMap(c->c.getServer())
				.flatMap(s->s.as(StarboardServer.type));
		}
	}
	
	private class StarboardMessageEditSpecImp implements StarboardMessageEditSpec {
		private DBObject object;

		public StarboardMessageEditSpecImp(DBObject object) {
			this.object = object;
		}

		@Override
		public void setStarboardMessage(Message message) {
			object.set("sbcid", message.getChannelID().getLong());
			object.set("sbmid", message.getMessageID().getLong());
		}
		
		@Override
		public void setOriginalMessage(Message message) {
			object.set("ogcid", message.getChannelID().getLong());
			object.set("ogmid", message.getMessageID().getLong());
		}
	}
}
