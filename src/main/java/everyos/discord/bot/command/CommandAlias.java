package everyos.discord.bot.command;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class CommandAlias implements ICommand {
    @Override public Mono<?> execute(Message chain, CommandData data, String argument) {
        return null;
    }
}