package everyos.discord.chat4d.functionality.channel;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.function.Consumer;

import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.message.EmbedSpec;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.discord.chat4d.entity.DiscordMessage;
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
				@Override public void setContent(String content) {
					setPresanitizedContent(content.replace("@", "@\u200E"));
				}
				
				@Override public void setPresanitizedContent(String content) {
					spec.setContent(content);
				}

				@Override public void addAttachment(String name, String imageURL) {
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
	
	private static class DiscordEmbedSpec implements EmbedSpec {
		private EmbedCreateSpec spec = null;
		private String footer = null;

		public DiscordEmbedSpec(EmbedCreateSpec spec) {
			this.spec = spec;
			spec.setTimestamp(Instant.now());
			//TODO: Method to set a timestamp
		}

		@Override
		public void setTitle(String title) {
			spec.setTitle(title);
		}

		@Override
		public void setColor(ChatColor color) {
			spec.setColor(Color.of(color.getRed(), color.getGreen(), color.getBlue()));
		}
		
		@Override
		public void setDescription(String description) {
			spec.setDescription(description);
		}

		@Override
		public void addField(String name, String content, boolean inline) {
			spec.addField(name, content, inline);
		}

		@Override
		public void setFooter(String footer) {
			this.footer = footer;
			updateFooter();
		}

		@Override
		public void setImage(String url) {
			spec.setImage(url);
		}
		
		private void updateFooter() {
			spec.setFooter(footer, null);
		}

		@Override
		public void setAuthor(String author, String url, String image) {
			spec.setAuthor(author, url, image);
		}
	}
}