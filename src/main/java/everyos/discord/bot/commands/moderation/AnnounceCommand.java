package everyos.discord.bot.commands.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.object.CategoryEnum;

public class AnnounceCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		//Smallest command ever (Method body is empty)
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Moderation;
	}
}
