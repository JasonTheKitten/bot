package everyos.bot.luwu.run.command.modules.welcome;

import java.util.Optional;

import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.event.MemberEvent;
import everyos.bot.luwu.core.entity.event.MemberJoinEvent;
import everyos.bot.luwu.core.entity.event.MemberLeaveEvent;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class WelcomeHooks {
	public static Mono<Void> welcomeHook(MemberEvent e) {
		if(e instanceof MemberJoinEvent) {
			return sendWelcomeMessage(e, true);
		} else if (e instanceof MemberLeaveEvent) {
			return sendWelcomeMessage(e, false);
		}
		return Mono.empty();
	}

	private static Mono<Void> sendWelcomeMessage(MemberEvent e, boolean isWelcome) {
		Member member = e.getMember();
		
		return member.getServer()
			.flatMap(server->server.as(WelcomeServer.type))
			.flatMap(server->{
				return server.getInfo().flatMap(info->{
					Optional<Tuple<ChannelID, String>> channelOp = isWelcome?
						info.getWelcomeMessage():
						info.getLeaveMessage();
						
					if (channelOp.isEmpty()) return Mono.empty();
					
					return channelOp.get().getT1().getChannel().flatMap(channel->{
						return channel.getInterface(ChannelTextInterface.class).send(spec->{
							spec.setPresanitizedContent(channelOp.get().getT2()
								.replace("${server.name}", server.getName())
								.replace("${user.name}", member.getHumanReadableID())
								.replace("@", "@\u200E")
								.replace("${user.ping}", "<@"+member.getID().toString()+">")
							+ "(Message set by server admin)");
						}); //TODO: Localize
							
						//TODO: Consider a message formatter class
					});
				});
			})
			.then();
	}
}
