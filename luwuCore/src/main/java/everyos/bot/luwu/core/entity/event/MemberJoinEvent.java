package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatMemberJoinEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class MemberJoinEvent extends MemberEvent {
	private ChatMemberJoinEvent joinEvent;

	public MemberJoinEvent(Connection connection, ChatMemberJoinEvent event) {
		super(connection, event);
		
		this.joinEvent = event;
	}
	
	public Member getMember() {
		return new Member(getConnection(), joinEvent.getMember());
	}

	public Mono<Server> getServer() {
		return joinEvent.getGuild().map(guild->new Server(getConnection(), guild));
	}
}
