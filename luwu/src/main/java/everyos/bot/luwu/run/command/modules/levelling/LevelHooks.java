package everyos.bot.luwu.run.command.modules.levelling;

import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import reactor.core.publisher.Mono;

public abstract class LevelHooks {
	private static final int COOLDOWN_SECONDS = 20;
	
	private LevelHooks() {}
	
	public static Mono<Void> levelHook(MessageCreateEvent event) {
		//TODO: Ensure that the guild is opted first.
		return event.getSenderAsMember().flatMap(senderMaybe->{
			if (!senderMaybe.isPresent()) return Mono.empty();
			Member sender = senderMaybe.get();
			if (sender.isBot()) return Mono.empty();
			
			sender.getServer()
				.map(server->server.getWithExtension(LevelServer.type));
			
			LevelMember member = sender.getWithExtension(LevelMember.type);
			return member.getLevelState().flatMap(oldLevelState->{
				long time = System.currentTimeMillis();
				if (oldLevelState.getTimestamp()+COOLDOWN_SECONDS*1000>time) {
					return Mono.empty();
				}
				LevelState newLevelState = new LevelState(oldLevelState.getXPTotal()+1,time);
				Mono<Void> m1 = Mono.empty();
				if (newLevelState.getXPLeveled()==0) {
					//TODO: Level messages
				}
				return m1.and(member.setLevelState(newLevelState));
			});
		});
	}
}
