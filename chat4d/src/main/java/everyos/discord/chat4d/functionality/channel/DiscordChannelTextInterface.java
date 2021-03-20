package everyos.discord.chat4d.functionality.channel;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import discord4j.core.object.entity.channel.MessageChannel;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.message.EmbedSpec;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.discord.chat4d.entity.DiscordMessage;
import everyos.discord.chat4d.functionality.DiscordEmbedSpec;
import reactor.core.publisher.Mono;

public class DiscordChannelTextInterface implements ChatChannelTextInterface {
	private MessageChannel channel;
	private ChatConnection connection;

	public DiscordChannelTextInterface(ChatConnection connection, MessageChannel channel) {
		this.channel = channel;
		this.connection = connection;
	}

	@Override public Mono<ChatMessage> send(String text) {
		return send(spec->spec.setContent(text));
	}
	@Override public Mono<ChatMessage> send(Consumer<MessageCreateSpec> func) {
		return channel.createMessage(spec->{
			func.accept(new MessageCreateSpec() {
				@Override
				public void setContent(String content) {
					setPresanitizedContent(content.replace("@", "@\u200E"));
				}
				
				@Override
				public void setPresanitizedContent(String content) {
					spec.setContent(content);
				}

				@Override
				public void addAttachment(String name, String imageURL) {
					try {
						spec.addFile(name, new URL(imageURL).openStream());
					} catch (IOException e) {
						//TODO:
						e.printStackTrace();
					}
				}

				@Override
				public void setEmbed(Consumer<EmbedSpec> embedSpec) {
					spec.setEmbed(eSpec->embedSpec.accept(new DiscordEmbedSpec(eSpec)));
				}
			});
		}).map(message->new DiscordMessage(connection, message));
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}

	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
}