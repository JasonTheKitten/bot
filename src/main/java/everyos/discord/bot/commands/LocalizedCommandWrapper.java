package everyos.discord.bot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.localization.Localization;

public class LocalizedCommandWrapper {
	private ICommand command;
	private Localization locale;

	public LocalizedCommandWrapper(ICommand command, Localization locale) {
		this.command = command;
		this.locale = locale;
	}
	
	public void execute(Message message, ChannelAdapter adapter) {
		command.execute(message, adapter, locale);
	}
}
