package everyos.bot.luwu.run.command.modules.leveling;

import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public abstract class LevelHooks {
	private static final int COOLDOWN_SECONDS = 20;
	
	private LevelHooks() {}
	
	public static Mono<Void> levelHook(MessageCreateEvent event) {
		return event.getSenderAsMember().flatMap(sender->{
			if (sender.isBot()) return Mono.empty();
			
			LevelMember member = sender.getWithExtension(LevelMember.type);
			
			return sender.getServer().flatMap(server->server.as(LevelServer.type)).flatMap(server->{
				return server.getLevelInfo().flatMap(info->{
					if (!info.getLevellingEnabled()) {
						return Mono.empty();
					}
					
					return member.getLevelState().flatMap(oldLevelState->{
						long time = System.currentTimeMillis();
						if (oldLevelState.getTimestamp()+COOLDOWN_SECONDS*1000>time) {
							return Mono.empty();
						}
						LevelState newLevelState = new LevelState(oldLevelState.getXPTotal()+1,time);
						Mono<Void> m1 = Mono.empty();
						if (newLevelState.getXPLeveled()==0) {
							/*locale.localize(
								"command.level.levelupmessage",
								"message", info.getLevelMessage(),
								"user.ping", "<@"+String.valueOf(sender.getID().getLong())+">",
								"user.level", String.valueOf(newLevelState.getLevel())
								);*/
							
							String resolved = info.getLevelMessage()
								.replace("${user.ping}", "<@"+String.valueOf(sender.getID().getLong())+">") //TODO
								.replace("${user.level}", String.valueOf(newLevelState.getLevel()));
								//TODO: XP, Previous Level, Server Name, User ID, User Name No Ping, etc
							m1 = info.getMessageChannelID().getChannel()
								.map(channel->channel.getInterface(ChannelTextInterface.class))
								.flatMap(tg->tg.send(spec->spec.setPresanitizedContent(resolved+" (Message set by guild admin)"))) //TODO: Localize
								.then();
						}
						return m1.and(member.setLevelState(newLevelState));
					});
				});
			});
		});
	}
}
