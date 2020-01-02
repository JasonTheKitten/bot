package everyos.discord.exobot.util;

import java.util.function.Consumer;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

public class MessageHelper {
	public static void send(Mono<MessageChannel> channel, String message, boolean permitPing) {
		send(channel.block(), message, permitPing);
	}
	public static Message send(MessageChannel channel, String message, boolean permitPing) {
		if (!permitPing) message = message.replace("@", "(Ping attempt)");
		return channel.createMessage(message).block();
	}
	public static Message send(MessageChannel channel, Consumer<? super EmbedCreateSpec> embed) {
		return channel.createEmbed(embed).block();
	}
}
