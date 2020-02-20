package everyos.discord.bot.commands;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.object.CategoryEnum;

public interface ICommand {
	void execute(Message message, MessageAdapter adapter, String argument);
    HashMap<String, ICommand> getSubcommands(Localization locale);
    String getBasicUsage(Localization locale);
    String getExtendedUsage(Localization locale);
	CategoryEnum getCategory();
}
