package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatMemberLeaveEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class MemberLeaveEvent extends MemberEvent {
	private ChatMemberLeaveEvent leaveEvent;

	public MemberLeaveEvent(Connection connection, ChatMemberLeaveEvent event) {
		super(connection, event);
		
		this.leaveEvent = event;
	}
	
	public Member getMember() {
		return new Member(getConnection(), leaveEvent.getMember());
	}

	public Mono<Server> getServer() {
		return leaveEvent.getGuild().map(guild->new Server(getConnection(), guild));
	}
}
