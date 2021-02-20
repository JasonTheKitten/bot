package everyos.bot.luwu.run.command.modules.utility;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class ProfileCommand extends CommandBase {
	public ProfileCommand() {
		super("command.profile", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, data.getInvoker(), locale)
			.flatMap(id->sendProfileEmbed(data.getChannel(), id, data.getInvoker(), locale));
	}

	private Mono<Void> sendProfileEmbed(Channel channel, UserID uid, User invoker, Locale locale) {
		return channel.getMember(uid).flatMap(member->{
			return channel.getInterface(ChannelTextInterface.class).send(spec->{	
				spec.setEmbed(embed->{
					ZonedDateTime time = Instant.ofEpochMilli(member.getJoinTimestamp()).atZone(ZoneId.of("GMT"));
					
					embed.setTitle("Profile - "+member.getHumanReadableID()); //TODO: Localize
					embed.setDescription("User information");
					member.getNickname().ifPresent(nick->embed.addField("Nickname", nick, true));
					embed.addField("Joined Server", time.getMonthValue()+"/"+time.getDayOfMonth()+"/"+time.getYear(), false);
					member.getAvatarUrl().ifPresent(url->embed.setImage(url));
					//TODO: Balance and level
					
					invoker.getAvatarUrl().ifPresent(url->embed.setAuthor(invoker.getHumanReadableID(), null, url));
				});
			});
		}).then();
	}

	private Mono<UserID> parseArgs(ArgumentParser parser, User invoker, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.just(invoker.getID());
		}
		
		if (!parser.couldBeUserID()) {
			return expect(locale, parser, "command.error.userid");
		}
		
		return Mono.just(parser.eatUserID());
	}
}

/*return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
return userm.flatMap(user->{
	return message.getAuthorAsMember().flatMap(invoker->{
		return channel.createEmbed(embed->{
			
		});
	});
});
});*/