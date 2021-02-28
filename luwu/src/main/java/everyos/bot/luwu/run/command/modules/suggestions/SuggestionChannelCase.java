package everyos.bot.luwu.run.command.modules.suggestions;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.channelcase.CommandChannelCase;
import everyos.bot.luwu.run.command.modules.channel.ResetChannelCommand;
import everyos.bot.luwu.run.command.modules.chatlink.moderation.LinkModerationCommands;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.run.command.modules.utility.SuggestCommand;
import reactor.core.publisher.Mono;

public class SuggestionChannelCase extends CommandChannelCase {
	private CommandContainer commands;

	@Override
	public CommandContainer getCommands() {
		this.commands = new CommandContainer();
		
		commands.category("default");
		LinkModerationCommands.installTo(commands);
		commands.registerCommand("command.resetchannel", new ResetChannelCommand());
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		
		commands.category("info");
		InfoCommands.installTo(commands);
		
		return commands;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(data, parser)
			.filter(v->!v)
			.flatMap(v->data.getChannel().as(SuggestionChannel.type))
			.flatMap(channel->channel.getOutputChannel())
			.flatMap(channel->SuggestCommand.sendSuggestion(channel, data.getInvoker(),
				data.getMessage().getContent().orElse("<empty message>"), data.getLocale())
				.then(channel.getInterface(ChannelTextInterface.class).send(spec->{
					spec.setPresanitizedContent("<@"+data.getMessage().getAuthorID().toString()+">");
				})))
			.flatMap(message->message.delete())
			.then(data.getMessage().delete())
			.then();
	}
	
	@Override
	public String getID() {
		return "command.suggestion.channelcase";
	}
	
	private static SuggestionChannelCase instance = new SuggestionChannelCase();
	
	public static SuggestionChannelCase get() {
		return instance;
	}
}
