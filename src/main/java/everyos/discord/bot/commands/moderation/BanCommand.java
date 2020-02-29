package everyos.discord.bot.commands.moderation;

import java.util.ArrayList;
import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.object.Promise;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.MemberDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;

public class BanCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->
			adapter.getMemberAdapter(madapter->{
				madapter.hasPermission(Permission.BAN_MEMBERS, perm->{
					if (!perm) {
						adapter.formatTextLocale(LocalizedString.InsufficientPermissions, str->cadapter.send(str));
						return;
					}
					
					ArgumentParser argp = new ArgumentParser(argument);
					if (argp.isEmpty()) {
						adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
						return;
					}
					
					ObjectStore success = new ObjectStore(true);
					Promise promise = new Promise(()->{
						if ((Boolean) success.object) {
							adapter.formatTextLocale(LocalizedString.BanSuccess, str->cadapter.send(str));
						}
						else adapter.formatTextLocale(LocalizedString.BanFail, str->cadapter.send(str));
					});
					ArrayList<String> ids = new ArrayList<String>();
					while (argp.couldBeUserID()) {
						ids.add(argp.eatUserID());
						promise.createResolver();
					}
					if (!argp.isEmpty()) {
						adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
					}
					promise.ready();
					adapter.getTopEntityAdapter(teadapter->
						ids.forEach(v->{
							MemberDocumentCreateStandard.ifExists(teadapter, v, bmadapter->{
								madapter.checkHigherThan(madapter, ht->{
									if (!ht) {
										success.object = false;
										promise.resolve();
										return;
									}
									bmadapter.ban(successb->{
										success.object = ((Boolean) success.object)&&successb;
										promise.resolve();
									});
								});
							}, ()->{
								success.object = false;
								promise.resolve();
							});
						})
					);
				});
			})
		);
	}
	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
		return null;
	}
	@Override public String getBasicUsage(Localization locale) {
		return "";
	}
	@Override public String getExtendedUsage(Localization locale) {
		return "";
	}
	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Moderation;
	}
}