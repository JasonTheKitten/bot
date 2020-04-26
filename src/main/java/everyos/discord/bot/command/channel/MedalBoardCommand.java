package everyos.discord.bot.command.channel;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import reactor.core.publisher.Mono;

public class MedalBoardCommand implements IGroupCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}

	@Override public HashMap<String, ICommand> getCommands(Localization locale) {
		return null;
	}
}
