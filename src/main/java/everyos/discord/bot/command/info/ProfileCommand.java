package everyos.discord.bot.command.info;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Image.Format;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.ProfileCommandHelp, ehelp=LocalizedString.ProfileCommandExtendedHelp, category=CategoryEnum.Utility)
public class ProfileCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			Mono<Member> userm = null;
			if (parser.couldBeUserID()) {
				userm = MemberAdapter.of(GuildAdapter.of(data.bot, channel), parser.eatUserID()).getMember()
					.onErrorResume(e->Mono.error(new LocalizedException(LocalizedString.UnrecognizedUser)));
			} else if (parser.isEmpty()) {
				userm = message.getAuthorAsMember();
			} else {
				return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			}
			
			return userm.flatMap(user->{
				return message.getAuthorAsMember().flatMap(invoker->{
					return channel.createEmbed(embed->{
						embed.setTitle("Profile - "+data.safe(user.getUsername())+"#"+user.getDiscriminator()); //TODO: Localize
						embed.setDescription("User information");
						embed.addField("Nickname", user.getDisplayName(), true);
						ZonedDateTime sdate = user.getJoinTime().atZone(ZoneId.of("GMT"));
						embed.addField("Joined Server", sdate.getMonthValue()+"/"+sdate.getDayOfMonth()+"/"+sdate.getYear(), false);
						invoker.getAvatarUrl(Format.PNG).ifPresent(url->embed.setAuthor(invoker.getUsername()+"#"+invoker.getDiscriminator(), null, url));
						//TODO: Balance and level
						user.getAvatarUrl(Format.PNG).ifPresent(url->embed.setImage(url));
					});
				});
			});
		});
	}
}
