package everyos.discord.bot.commands.info;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.Main;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.util.FillinUtil;

public class PingCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.formatTextLocale(
				LocalizedString.Ping, 
				FillinUtil.of("ping", String.valueOf(Main.client.getResponseTime())), 
				text->cadapter.send(text));
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }

	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Info;
	}
}
