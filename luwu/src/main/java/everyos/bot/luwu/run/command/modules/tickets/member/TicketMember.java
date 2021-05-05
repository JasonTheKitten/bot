package everyos.bot.luwu.run.command.modules.tickets.member;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;
import reactor.core.publisher.Mono;

public class TicketMember extends Member {
	public TicketMember(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		super(connection, member, documents);
	}
	
	public Mono<TicketMemberInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new TicketMemberInfoImp(doc.getObject());
		});
	}
	
	public Mono<Void> edit(Consumer<TicketMemberEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			func.accept(new TicketMemberEditSpecImp(doc.getObject()));
			
			return doc.save();
		});
	}
	
	private class TicketMemberInfoImp implements TicketMemberInfo {
		private DBObject object;

		public TicketMemberInfoImp(DBObject object) {
			this.object = object;
		}
		
		@Override
		public Optional<ChannelID> getTicketChannelID() {
			if (!object.has("ticket")) return Optional.empty();
			return Optional.of(new ChannelID(
				getConnection(),
				object.getOrDefaultLong("ticket", -1L),
				getConnection().getClient().getID()));
		}

		@Override
		public Mono<Channel> getTicketChannel() {
			return getTicketChannelID().map(id->id.getChannel()).orElse(Mono.empty());
		}
	}
	
	private class TicketMemberEditSpecImp implements TicketMemberEditSpec {
		private DBObject object;

		public TicketMemberEditSpecImp(DBObject object) {
			this.object = object;
		}
		
		@Override
		public void setTicketChannel(ChannelID channel) {
			object.set("ticket", channel.getLong());
		}

		@Override
		public void clearTicketChannel() {
			object.remove("ticket");
		}
	}
}
