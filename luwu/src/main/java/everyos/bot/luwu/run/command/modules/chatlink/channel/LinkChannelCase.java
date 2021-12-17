package everyos.bot.luwu.run.command.modules.chatlink.channel;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.channelcase.CommandChannelCase;
import everyos.bot.luwu.run.command.modules.channel.ResetChannelCommand;
import everyos.bot.luwu.run.command.modules.chatlink.link.Link;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.LinkModerationCommands;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.LinkSendCommand;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.WarnedMuteCommandWrapper;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import reactor.core.publisher.Mono;

public class LinkChannelCase extends CommandChannelCase {
	
	private static LinkChannelCase instance = new LinkChannelCase();
	
	private CommandContainer commands;

	public LinkChannelCase() {
		this.commands = new CommandContainer();
		
		commands.category("default");
		LinkModerationCommands.installTo(commands);
		commands.registerCommand("command.resetchannel", new ResetChannelCommand());
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		
		// Override the mute command with a warning
		commands.registerCommand("command.mute", new WarnedMuteCommandWrapper(true));
		commands.registerCommand("command.unmute", new WarnedMuteCommandWrapper(false));
		
		commands.category("info");
		InfoCommands.installTo(commands);
	}
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Link.onChannelReceivedMessage(data.getChannel().getID());
		
		return runCommands(data, parser)
			.filter(v->!v)
			.flatMap(_1 -> new LinkSendCommand().execute(data, parser));
	}
	
	@Override
	public CommandContainer getCommands() {
		return commands;
	}
	
	@Override
	public String getID() {
		return "command.link.channelcase";
	}
	
	public static LinkChannelCase get() {
		return instance;
	}
	
}
