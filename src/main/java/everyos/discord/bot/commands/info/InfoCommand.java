package everyos.discord.bot.commands.info;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;

public class InfoCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getTextLocale(locale->{
				cadapter.sendEmbed(embed->{
                    embed.setTitle(adapter.formatTextLocale(locale, LocalizedString.Info));
                    embed.setDescription(adapter.formatTextLocale(locale, LocalizedString.InfoDescription));
				});
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Info;
	}
}
