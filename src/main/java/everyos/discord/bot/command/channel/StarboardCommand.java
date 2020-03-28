package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.GuildMessageChannel;
import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class StarboardCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			GuildAdapter.of(data.shard, channel.getGuildId().asString()).getDocument().getObject((obj, doc)->{
				obj.set("starc", parser.eatChannelID());
				
				boolean isID = parser.couldBeEmojiID();
				String reactID = isID?parser.eatEmojiID():parser.eat(); //TODO
				
				obj.set("star", reactID);
				
				doc.save();
			});
			
			return channel.createMessage(data.localize(LocalizedString.StarboardSet));
		});
	}
}
