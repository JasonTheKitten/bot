package everyos.discord.bot.command.moderation;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.IGroupCommand;
import reactor.core.publisher.Mono;

public class OneWordModerationCommand implements IGroupCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
