package everyos.discord.chat4d.functionality.message;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.message.ChatMessageReactionInterface;
import everyos.discord.chat4d.entity.DiscordUser;
import reactor.core.publisher.Mono;

public class DiscordMessageReactionInterface implements ChatMessageReactionInterface {
	private ChatConnection connection;
	private Message message;

	public DiscordMessageReactionInterface(ChatConnection connection, Message message) {
		this.connection = connection;
		this.message = message;
	}
	
	@Override
	public ChatConnection getConnection() {
		return this.connection;
	}

	@Override
	public ChatClient getClient() {
		return connection.getClient();
	}

	@Override
	public Mono<Void> addReaction(String name) {
		return message.addReaction(ReactionEmoji.unicode(name));
	}

	@Override
	public Mono<Void> addReaction(long id) {
		return message.addReaction(ReactionEmoji.of(id, "emoji", true));
	}

	@Override
	public Mono<Void> removeReaction(String name) {
		return message.removeSelfReaction(ReactionEmoji.unicode(name));
	}

	@Override
	public Mono<Void> removeReaction(long id) {
		return message.removeSelfReaction(ReactionEmoji.of(id, "emoji", true));
	}

	@Override
	public Mono<ChatUser[]> getReactions(String name) {
		return message.getReactors(ReactionEmoji.unicode(name))
			.map(user->new DiscordUser(connection, user))
			.collectList()
			.map(list->list.toArray(new ChatUser[list.size()]));
	}
	
	@Override
	public Mono<ChatUser[]> getReactions(long id) {
		return message.getReactors(ReactionEmoji.of(id, "emoji", true))
			.map(user->new DiscordUser(connection, user))
			.collectList()
			.map(list->list.toArray(new ChatUser[list.size()]));
	}
}
