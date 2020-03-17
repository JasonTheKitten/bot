package everyos.discord.bot.command;


import discord4j.core.object.entity.Message;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

public class InvalidSubcommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->channel.createMessage(data.safe(LocalizedString.NoSuchSubcommand)));
    }
}