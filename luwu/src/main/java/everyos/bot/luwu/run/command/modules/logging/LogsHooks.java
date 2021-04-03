package everyos.bot.luwu.run.command.modules.logging;

import everyos.bot.luwu.core.entity.event.MessageDeleteEvent;
import everyos.bot.luwu.core.entity.event.MessageEditEvent;
import everyos.bot.luwu.core.entity.event.MessageEvent;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class LogsHooks {
	public static Mono<Void> logsHook(MessageEvent e) {
		//TODO: Localize everything
		return e.getServer()
			.flatMap(server->server.as(LogsServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(info->info.getLogChannel())
			.map(channel->channel.getInterface(ChannelTextInterface.class))
			.flatMap(logChannel->{
				if (e instanceof MessageEditEvent) {
					return processEvent(logChannel, (MessageEditEvent) e);
				} else if (e instanceof MessageDeleteEvent) {
					return processEvent(logChannel, (MessageDeleteEvent) e);
				}
				return Mono.empty();
			});
	}

	private static Mono<Void> processEvent(ChannelTextInterface logChannel, MessageEditEvent e) {
		return e.getMessage().flatMap(message->{
			return message.getAuthor().flatMap(author->{
				if (author.isBot()) return Mono.empty();
				return logChannel.send(spec->{
					String oldContent = ((MessageEditEvent) e).getOldMessage()
						.flatMap(msg->msg.getContent())
						.orElse("");
					
					spec.setEmbed(embed->{
						embed.setTitle("Message Editted");
						StringBuilder desc = new StringBuilder();
						if (oldContent.isEmpty()) {
							desc.append("**Old Message Unavailable**\n");
						} else {
							desc.append("**Old Message:** "+oldContent+'\n');
						}
						desc.append("**New Message:** ");
						desc.append(message.getContent().orElse(""));
						//TODO: Test everyone pings in an embed
						embed.setDescription(desc.toString());
						embed.setFooter("User ID: "+author.getID().toString());
					});
					message.getContent();
				});
			});
		}).then();
	}
	
	private static Mono<Void> processEvent(ChannelTextInterface logChannel, MessageDeleteEvent e) {
		return e.getMessage()
			.map(message->Tuple.of(message.getContent().orElse(""), message.getAuthorID().toString()))
			.switchIfEmpty(Mono.just(Tuple.of("", null)))
			.flatMap(message->{
				if (message.getT1().isEmpty()) return Mono.empty();
				return logChannel.send(spec->{
					spec.setEmbed(embed->{
						embed.setTitle("Message Deleted");
						/*if (message.getT1().isEmpty()) {
							embed.setDescription("**Old Message Unavailable**");
						} else {*/
							embed.setDescription(message.getT1());
							embed.setFooter("User ID: "+message.getT2());
						//}
					});
				});
			})
			.then();
	}
}
