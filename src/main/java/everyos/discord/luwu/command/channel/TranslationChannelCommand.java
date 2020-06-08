package everyos.discord.luwu.command.channel;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Ignorable;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import reactor.core.publisher.Mono;

@Ignorable(id=9)
public class TranslationChannelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
