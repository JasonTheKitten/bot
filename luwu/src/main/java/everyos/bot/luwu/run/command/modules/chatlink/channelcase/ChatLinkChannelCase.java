package everyos.bot.luwu.run.command.modules.chatlink.channelcase;

import java.time.Duration;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.channelcase.CommandChannelCase;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.LinkModerationCommands;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.WarnedMuteCommandWrapper;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import reactor.core.publisher.Mono;

public class ChatLinkChannelCase extends CommandChannelCase {
	private static ChatLinkChannelCase instance = new ChatLinkChannelCase();
	
	private CommandContainer commands;

	public ChatLinkChannelCase() {
		this.commands = new CommandContainer();
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		LinkModerationCommands.installTo(commands);
		
		// Override the mute command with a warning
		commands.registerCommand("command.mute", new WarnedMuteCommandWrapper(true));
		commands.registerCommand("command.unmute", new WarnedMuteCommandWrapper(false));
		
		commands.category("info");
		InfoCommands.installTo(commands);
	}
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//Collections.synchronizedMap(new WeakHashMap<Object, Object>());
		Locale locale = data.getLocale();
		
		return runCommands(data, parser)
			.filter(v->!v)
			.flatMap(v->data.getChannel().as(ChatLinkChannel.type))
			.flatMap(clchannel->{
				return clchannel.getLink()
					.flatMap(link->{
						return clchannel.getInfo()
							.map(info->info.isVerified())
							.filter(v2->v2)
							.switchIfEmpty(Mono.error(new TextException(locale.localize("command.link.error.needverified"))))
							.flatMap(v2->link.sendMessage(data.getMessage()));
					})
					.then(data.getMessage().addReaction(EmojiID.of("\u2611")))
					.thenReturn(true) // .delayElement requires a non-empty mono
					.delayElement(Duration.ofMillis(1000))
					.then(data.getMessage().removeReaction(EmojiID.of("\u2611")))
					.onErrorResume(e->{
						Mono<Void> m1 = data.getMessage().addReaction(EmojiID.of("\u274C"));
						if (e instanceof TextException) {
							m1=m1.and(clchannel.getInterface(ChannelTextInterface.class).send(e.getMessage()));
						} else {
							e.printStackTrace();
						}
						return m1;
					});
			})
			.then();
	}
	
	@Override
	public CommandContainer getCommands() {
		return commands;
	}
	
	@Override
	public String getID() {
		return "command.link.channelcase";
	}

	public static ChatLinkChannelCase get() { //TODO: No need to make this a singleton
		return ChatLinkChannelCase.instance ;
	}
}
