package everyos.discord.bot.commands.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;

public class PurgeCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getMemberAdapter(madapter->{
				madapter.hasPermission(Permission.MANAGE_MESSAGES, hp->{
					if (!hp) {
						adapter.formatTextLocale(LocalizedString.InsufficientPermissions, str->cadapter.send(str));
						return;
					}
					
					adapter.getTopEntityAdapter(teadapter->{
						ArgumentParser parser = new ArgumentParser(argument);
						String amountstr = parser.next();
						//TODO
					});
				});
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
		return null;
	}
	@Override public String getBasicUsage(Localization locale) {
		return null;
	}
	@Override public String getExtendedUsage(Localization locale) {
		return null;
	}
	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Moderation;
	}
}