package everyos.bot.luwu.run.command.modules.tickets.member;

import java.util.Optional;

import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import reactor.core.publisher.Mono;

public interface TicketMemberInfo {
	
	Optional<ChannelID> getTicketChannelID();
	Mono<Channel> getTicketChannel();
	
}
