package everyos.discord.luwu.command.moderation;

import java.util.ArrayList;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.MemberAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.BotPermissionUtil;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.BanCommandHelp, ehelp = LocalizedString.BanCommandExtendedHelp, category=CategoryEnum.Moderation)
public class BanCommand implements ICommand {
	//TODO: Support Mass Ban (Raid Prevention)
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			return BotPermissionUtil.check(channel, new Permission[] {Permission.BAN_MEMBERS})
				.then(message.getAuthorAsMember())
				.flatMap(m->PermissionUtil.check(m, Permission.BAN_MEMBERS))
				.flatMap(invoker->{
					ArgumentParser argp = new ArgumentParser(argument);
					if (argp.isEmpty()) {
						return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					}
					
					ArrayList<Long> ids = new ArrayList<Long>();
					while (argp.couldBeUserID()) {
						ids.add(argp.eatUserID());
					}
					Flux<?> idf = Flux.just(ids.toArray());
					if (!argp.isEmpty()) {
						return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					}
					return idf.flatMap(id->{
						return 
							invoker.isHigher(Snowflake.of((Long) id))
							.flatMap(isHigher->{
								if (!isHigher) return Mono.empty();
								return MemberAdapter.of(GuildAdapter.of(data.bot, channel), (Long) id).getMember();
							});
					})
					.flatMap(m->m.ban(b->{
						//TODO: Mod logs
						b.setReason(data.localize(LocalizedString.BanUserReason));
						b.setDeleteMessageDays(0);
					}))
					.then(Mono.just(data.localize(LocalizedString.BanSuccess)))
					.flatMap(s->channel.createMessage(s))
					.onErrorResume(o->channel.createMessage(data.localize(LocalizedString.BanFail)));
				});
		});
	}
}
