package everyos.discord.bot.command.moderation;

import java.util.ArrayList;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.TopEntityAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BanCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember().flatMap(invoker->{
				return invoker.getBasePermissions() .flatMap(perms->{
					if (!(perms.contains(Permission.ADMINISTRATOR)||perms.contains(Permission.BAN_MEMBERS)))
                        return channel.createMessage(data.locale.localize(LocalizedString.InsufficientPermissions));
					
					ArgumentParser argp = new ArgumentParser(argument);
					if (argp.isEmpty()) {
						return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					}
					
					ArrayList<String> ids = new ArrayList<String>();
					while (argp.couldBeUserID()) {
						ids.add(argp.eatUserID());
					}
					Flux<?> idf = Flux.just(ids.toArray());
					if (!argp.isEmpty()) {
						return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					}
					TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
					if (!teadapter.isOfGuild()) return Mono.empty(); //TODO
					GuildAdapter gadapter = (GuildAdapter) teadapter.getPrimaryAdapter();
					
					return idf.flatMap(id->{
						return 
							invoker.isHigher(Snowflake.of((String) id))
							.flatMap(isHigher->{
								if (!isHigher) return Mono.empty();
								return MemberAdapter.of(gadapter, (String) id).getMember();
							});
					})
					.flatMap(m->m.ban(b->{
						//TODO: Mod logs
						b.setReason("Todo: Localized message explaining moderator invocation"); //TODO
						b.setDeleteMessageDays(0);
					}))
					.then(Mono.just(data.localize(LocalizedString.BanSuccess)))
					.flatMap(s->channel.createMessage(s))
					.onErrorResume(o->channel.createMessage(data.localize(LocalizedString.BanFail)));
				});
			});
		});
	}
}
