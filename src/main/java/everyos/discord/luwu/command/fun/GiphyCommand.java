package everyos.discord.luwu.command.fun;

import com.kdotj.simplegiphy.SimpleGiphy;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
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
