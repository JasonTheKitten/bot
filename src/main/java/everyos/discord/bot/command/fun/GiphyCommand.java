package everyos.discord.bot.command.fun;

import com.kdotj.simplegiphy.SimpleGiphy;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.GiphyCommandHelp, ehelp = LocalizedString.GiphyCommandExtendedHelp, category=CategoryEnum.Fun)
public class GiphyCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			SimpleGiphy.setApiKey(data.bot.giphyKey);
			if (data.bot.giphyKey==null) return channel.createMessage(data.localize(LocalizedString.NoGiphyKey));
			return channel.createEmbed(embed->{
				String search = argument.isEmpty()?"cat":argument;
				embed.setTitle("Giphy - "+search);
				try {
					embed.setImage(SimpleGiphy.getInstance().random(search, "g").getRandomGiphy().getImageUrl());
				} catch (Exception e) {
					embed.addField("Error", "An error has occured", false);
				}
				embed.setFooter("Powered by Giphy", null);
			});
		});
	}
}
