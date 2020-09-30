package everyos.bot.luwu.event;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.luwu.BotInstance;
import everyos.bot.luwu.client.ClientBehaviour;
import everyos.bot.luwu.command.CommandDataImp;
import everyos.bot.luwu.command.usercase.DefaultUserCase;
import everyos.bot.luwu.database.Database;
import reactor.core.publisher.Mono;

public class MessageCreateEventProcessor implements Function<ChatMessageCreateEvent, Publisher<Object>> {
	private ClientBehaviour behaviour;
	private BotInstance bot;
	private Database database;

	public MessageCreateEventProcessor(BotInstance bot, Database database, ClientBehaviour behaviour) {
		this.behaviour = behaviour;
		this.bot = bot;
		this.database = database;
	}

	@Override public Mono<Object> apply(ChatMessageCreateEvent event) {
		// Step 1: Determine the user type. For now, we will skip DB queries
		
		return event.getMessage().flatMap(message->{
			return message.getChannel().flatMap(channel->{
				return event.getSender()
					.flatMap(sender->sender.asMemberOf(channel))
					.map(sender-> {
						//So here, we make the data we will pass to commands
						return new CommandDataImp(bot, database, message, sender, channel);
					});
			});
		}).flatMap(data->{
			Mono<?> cc = DefaultUserCase.get() //We execute the default command
				.execute(data, behaviour.createParser(data.getMessage().getContent().orElse("")));
			
			//TODO: Need more actions? DB queries? myAction().then(cc);
			
			return cc;
		});
	}
}
