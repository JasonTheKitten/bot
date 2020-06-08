package everyos.discord.luwu.command.utility;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

@Help(category=CategoryEnum.Utility)
public class EmbedCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			String title = null;
			String desc = null;
			ArrayList<Pair<String, String>> fields = new ArrayList<Pair<String, String>>();
			
			ArgumentParser parser = new ArgumentParser(argument);
			while (!parser.isEmpty()) {
				String flag = parser.eat();
				if (flag.equals("-t")||flag.equals("--title")) {
					if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					title = parser.eatQuote();
				} else if (flag.equals("-d")||flag.equals("--desc")||flag.equals("--description")) {
					if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					desc = parser.eatQuote();
				} else if (flag.equals("-f")||flag.equals("--field")) {
					if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					String t1 = parser.eatQuote();
					if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					String t2 = parser.eatQuote();
					fields.add(Pair.of(t1, t2));
				}
			}
			
			String ftitle = title;
			String fdesc = desc;
			return channel.createEmbed(embed->{
				if (ftitle!=null) embed.setTitle(data.safe(ftitle));
				if (fdesc!=null) embed.setDescription(data.safe(fdesc));
				fields.forEach(f->embed.addField(data.safe(f.getLeft()), data.safe(f.getRight()), false));
				message.getAuthor().ifPresent(author->embed.setFooter("User ID: "+author.getId().asString(), null)); //TODO: Localize
			});
		});
	}
}
