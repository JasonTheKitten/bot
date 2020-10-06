package everyos.bot.luwu;

import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import ch.qos.logback.classic.Logger;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.luwu.client.ClientBehaviour;
import everyos.bot.luwu.database.Database;
import everyos.bot.luwu.event.MessageCreateEventProcessor;
import everyos.bot.luwu.parser.ArgumentParser;
import everyos.bot.luwu.parser.DiscordArgumentParser;
import everyos.bot.luwu.parser.NertiviaArgumentParser;
import everyos.discord.chat4d.DiscordChatClient;
import everyos.nertivia.chat4n.NertiviaChatClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotInstance {
	private Configuration configuration;
	@SuppressWarnings("unused")
	private Logger logger;
	private Database database; //V4 goal: Reduce exposure of the database to commands, increasing cohesion

	public BotInstance(Configuration configs) {
		this.configuration = configs;
		this.database = Database.of(
			configuration.getDatabaseURL()
				.replace("<password>", configuration.getDatabasePassword()),
			configuration.getDatabaseName());
	}
	
	//Our primary method
	public Mono<Void> execute() {
		Mono<?> m = Mono.empty();
		
		String discordToken = configuration.getDiscordToken();
		if (discordToken!=null) {
			m = m.and(createDiscordClient(discordToken));
		}
		
		String nertiviaToken = configuration.getNertiviaToken();
		if (nertiviaToken!=null) {
			m = m.and(createNertiviaClient(nertiviaToken));
		}
		
		return m.then();
	}
	
	//Code below is used for create Nertivia and Discord clients
	private Mono<?> createDiscordClient(String token) {
		DiscordChatClient client = new DiscordChatClient(token);
		return client.login(createHandlers(new ClientBehaviour() {
			@Override public ArgumentParser createParser(String argument) {
				return new DiscordArgumentParser(argument);
			}
		}));
	}
	private Mono<?> createNertiviaClient(String token) {
		NertiviaChatClient client = new NertiviaChatClient(token);
		return client.login(createHandlers(new ClientBehaviour() {
			@Override public ArgumentParser createParser(String argument) {
				return new NertiviaArgumentParser(argument);
			}
		}));
	}
	
	//Creating event handlers
	private Function<ChatConnection, Mono<?>> createHandlers(ClientBehaviour behaviour) {
		return connection->{
			//Message event handler
			Flux<?> m1 = this.<ChatMessageCreateEvent, Object>handleErrors(
				connection.generateEventListener(ChatMessageCreateEvent.class), 
				(Function<ChatMessageCreateEvent, Publisher<Object>>) new MessageCreateEventProcessor(this, database, behaviour));
			
			return Mono.when(m1);
		};
	}
	
	//Error handling
	private <T, T2> Flux<T2> handleErrors(Flux<T> f, Function<T, Publisher<T2>> fun) {
    	return f.flatMap(t->{
    		Publisher<T2> p = fun.apply(t);
    		if (p instanceof Flux) return ((Flux<T2>) p).onErrorResume(e->{
    			e.printStackTrace();
    			return Flux.empty();
    		});
    		if (p instanceof Mono) return ((Mono<T2>) p).onErrorResume(e->{
    			e.printStackTrace();
    			return Mono.empty();
    		});
    		return p;
    	});
    }
    
    @SuppressWarnings("unused")
	private <T> Flux<T> handleErrorsNR(Flux<T> f, Consumer<T> fun) {
    	return handleErrors(f, t->{
    		fun.accept(t);
    		return Mono.just(t);
    	});
    }
}
