package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import reactor.core.publisher.Mono;

public abstract class FethHooks {
	private static final int COOLDOWN_SECONDS = 20;
	
	private FethHooks() {}
	
	public static Mono<Void> fethHook(MessageCreateEvent event) {
		return event.getSenderAsMember().flatMap(sender->{
			if (sender.isBot()) return Mono.empty();
			
			FethMember member = sender.as(FethMember.type);
			
			return sender.getServer().flatMap(server->server.as(FethServer.type)).flatMap(server->{
				return server.getCurrencyInfo().flatMap(info->{
					if (!info.getCurrencyEnabled()) {
						return Mono.empty();
					}
					
					return member.edit(spec->{
						long time = System.currentTimeMillis();
						if (spec.getInfo().getTimeout()+COOLDOWN_SECONDS*1000>time) {
							return Mono.empty();
						}

						spec.setCurrency(spec.getInfo().getCurrency()+1);
						spec.setTimeout(time);
						
						return Mono.empty();
					});
				});
			});
		});
	}
}
