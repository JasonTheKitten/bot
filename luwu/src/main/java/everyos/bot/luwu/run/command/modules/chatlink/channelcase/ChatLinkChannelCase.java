package everyos.bot.luwu.run.command.modules.chatlink.channelcase;

import java.time.Duration;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.run.command.channelcase.CommandChannelCase;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.LinkModerationCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import reactor.core.publisher.Mono;

public class ChatLinkChannelCase extends CommandChannelCase {
	private static ChatLinkChannelCase instance = new ChatLinkChannelCase();
	
	private CommandContainer commands;

	public ChatLinkChannelCase() { //TODO: Accept pre-made CommandContainer within constructor
		this.commands = new CommandContainer();
		ModerationCommands.installTo(commands);
		LinkModerationCommands.installTo(commands);
	}
	
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//Collections.synchronizedMap(new WeakHashMap<Object, Object>());
		return runCommands(commands, data, parser)
			.filter(v->!v)
			.flatMap(v->data.getChannel().as(ChatLinkChannel.type))
			.flatMap(clchannel->{
				return clchannel.getLink()
					.flatMap(link->{
						return Mono.just(clchannel.isVerified())
							.filter(v2->v2)
							.flatMap(v2->link.sendMessage(data.getMessage()))
							.switchIfEmpty(Mono.error(new TextException("link.error.needverified")));
					})
					.then(data.getMessage().addReaction("\u2611"))
					.delayElement(Duration.ofMillis(1000))
					.then(data.getMessage().removeReaction("\u2611"))
					.onErrorResume(e->{
						return data.getMessage().addReaction("x");
					});
			})
			.then();
	}

	public static ChatLinkChannelCase get() { //TODO: No need to make this a singleton
		return ChatLinkChannelCase.instance ;
	}
}
