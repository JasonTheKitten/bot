package everyos.discord.bot.command.info;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Image.Format;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.TopEntityAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.ProfileCommandHelp, ehelp=LocalizedString.ProfileCommandExtendedHelp)
public class ProfileCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
			if (!teadapter.isOfGuild()) return Mono.empty(); //TODO
			GuildAdapter gadapter = (GuildAdapter) teadapter.getPrimaryAdapter();
			
			ArgumentParser parser = new ArgumentParser(argument);
			Mono<Member> userm = null;
			if (parser.couldBeUserID()) {
				userm = MemberAdapter.of(gadapter, parser.eatUserID()).getMember(); //TODO: ResumeOnError
			} else if (parser.isEmpty()) {
				userm = message.getAuthorAsMember();
			} else {
				return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			}
			
			return userm.flatMap(user->{
				return message.getAuthorAsMember().flatMap(invoker->{
					return channel.createEmbed(embed->{
						embed.setTitle("Profile - "+user.getUsername()+"#"+user.getDiscriminator()); //TODO: Localize & Filter
						embed.setDescription("User information");
						embed.addField("Nickname", user.getDisplayName(), true);
						//TODO: embed.addField("Joined", user.getJoinTime()., inline)
						invoker.getAvatarUrl(Format.PNG).ifPresent(url->embed.setAuthor(invoker.getUsername()+"#"+invoker.getDiscriminator(), null, url));
						//TODO: Balanace and level
						user.getAvatarUrl(Format.PNG).ifPresent(url->embed.setImage(url));
					});
				});
			});
		});
	}
}
