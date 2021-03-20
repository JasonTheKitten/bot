package everyos.bot.luwu.core.entity.imp;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.message.ChatMessageReactionInterface;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.functionality.message.MessageReactionInterface;
import reactor.core.publisher.Mono;

public class MessageReactionInterfaceImp implements MessageReactionInterface {
	private Connection connection;
	private ChatMessageReactionInterface message;
	
	public MessageReactionInterfaceImp(Connection connection, ChatMessageReactionInterface message) {
		this.connection = connection;
		this.message = message;
	}
	
	@Override
	public Mono<Void> addReaction(EmojiID reaction) {
		if (reaction.getID().isPresent()) {
			return message.addReaction(reaction.getID().get());
		} else if (reaction.getName().isPresent()) {
			return message.addReaction(reaction.getName().get());
		}
		return Mono.empty();
		
	}
	
	@Override
	public Mono<Void> removeReaction(EmojiID reaction) {
		if (reaction.getID().isPresent()) {
			return message.removeReaction(reaction.getID().get());
		} else if (reaction.getName().isPresent()) {
			return message.removeReaction(reaction.getName().get());
		}
		return Mono.empty();
	}
	
	@Override
	public Mono<User[]> getReactors(EmojiID reaction) {
		Mono<ChatUser[]> reactorMono = reaction.getName().isPresent()?
			message.getReactions(reaction.getName().get()):
			message.getReactions(reaction.getID().get());
			
		return reactorMono.map(reactorsRaw->{
			User[] reactors = new User[reactorsRaw.length];
			
			for (int i=0; i<reactorsRaw.length; i++) {
				reactors[i] = new User(connection, reactorsRaw[i]);
			}
			
			return reactors;
		});
	}
	
	@Override
	public Connection getConnection() {
		return connection;
	}
	@Override
	public Client getClient() {
		return connection.getClient();
	}
}
