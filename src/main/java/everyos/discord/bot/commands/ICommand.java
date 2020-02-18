package everyos.discord.bot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.object.HelpObject;

public interface ICommand {
	void execute(Message message, ChannelAdapter adapter, Localization locale);
	HelpObject getHelp(Localization locale, String argument);
	CategoryEnum getCategory();
}
