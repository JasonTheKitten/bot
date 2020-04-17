package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.annotation.Ignorable;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.storage.database.DBObject;
import reactor.core.publisher.Mono;

@Ignorable(id=7)
@Help(help=LocalizedString.StarboardCommandHelp, ehelp=LocalizedString.StarboardCommandExtendedHelp, category=CategoryEnum.Channel)
public class StarboardCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.shard, channel.getGuildId().asLong()).getData((obj, doc)->{
				if (!parser.couldBeChannelID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				obj.set("starc", parser.eatChannelID());
				
				if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				boolean isID = parser.couldBeEmojiID();
				String reactID = isID?parser.eatEmojiID():parser.eat(); //TODO
				obj.set("star", reactID);
				
				DBObject starSets = new DBObject();
				
				doc.save();
				
				return Mono.empty();
			}).then(channel.createMessage(data.localize(LocalizedString.StarboardSet)));
		});
	}
}
