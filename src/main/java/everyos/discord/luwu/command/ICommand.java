package everyos.discord.luwu.command;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface ICommand {
    Mono<?> execute(Message message, CommandData data, String argument);
}