package everyos.bot.luwu.run.command.modules.tickets.member;

import everyos.bot.luwu.core.entity.ChannelID;

public interface TicketMemberEditSpec {
	void setTicketChannel(ChannelID channel);
	void clearTicketChannel();
}
