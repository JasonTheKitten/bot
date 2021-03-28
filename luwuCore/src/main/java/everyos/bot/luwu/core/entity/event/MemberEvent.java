package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;

public abstract class MemberEvent extends Event {
	public MemberEvent(Connection connection, ChatEvent event) {
		super(connection, event);
	}
	
	public abstract Member getMember();
}
