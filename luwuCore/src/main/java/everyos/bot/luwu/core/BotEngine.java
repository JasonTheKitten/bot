package everyos.bot.luwu.core;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.UserCase;
import everyos.bot.luwu.core.database.DBDatabase;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.ClientWrapper;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import everyos.bot.luwu.core.event.MessageCreateEventProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotEngine {
	private Client[] clients;
	private BotEngineConfiguration configuration;
	private HashMap<Integer, Connection> connections;
	
	public BotEngine(BotEngineConfiguration configuration) {
		this.configuration = configuration;
		this.connections = new HashMap<>();
		
		final ClientWrapper[] clientWrappers = configuration.getClients();
		clients = new Client[clientWrappers.length];
		int i = 0;
		while (i<clients.length) {
			clients[i] = clientWrappers[i++].create(this);
		}
	}

	public Mono<Void> start() {
		Mono<Void> mono = Mono.empty();
		for(Client client: clients) {
			mono = mono.and(client.login(connection->{
				connections.put(client.getID(), connection);
				return createHandlers(connection);
			}));
		}	
		return mono;
	}

	//Creating event handlers
	private static Mono<Void> createHandlers(Connection connection) {
		//Message event handler
		Flux<?> m1 = BotEngine.<MessageCreateEvent, Void>handleErrors(
			connection.generateEventListener(MessageCreateEvent.class), 
			MessageCreateEventProcessor::apply);
		
		return Mono.when(m1);
	}

	//Error handling
	private static <T, T2> Flux<T2> handleErrors(Flux<T> f, Function<T, Publisher<T2>> fun) {
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
	private static <T> Flux<T> handleErrorsNR(Flux<T> f, Consumer<T> fun) {
		return handleErrors(f, t->{
			fun.accept(t);
			return Mono.just(t);
		});
	}

	public Mono<ChannelCase> getChannelCase(CommandData data) {
		Mono<String> channelCaseNameMono = Mono.just(data.getChannel().getType());
		for (BiFunction<Mono<String>, CommandData, Mono<String>> func: configuration.getChannelCaseTransformers()) {
			channelCaseNameMono = func.apply(channelCaseNameMono, data);
		}
		return channelCaseNameMono.map(name->configuration.getChannelCase(name));
	}
	
	public Mono<UserCase> getUserCase(CommandData data) {
		// TODO: Look up the user case in database, and only use default if not present
		Mono<String> userCaseNameMono = Mono.just(configuration.getDefaultUserCaseName());
		for (BiFunction<Mono<String>, CommandData, Mono<String>> func: configuration.getUserCaseTransformers()) {
			userCaseNameMono = func.apply(userCaseNameMono, data);
		}
		return userCaseNameMono.map(name->configuration.getUserCase(name));
	}

	//TODO: This doesn't feel like the right place to put this
	//I would prefer to hide the database as much as possible, to be honest
	//Putting this in BotEngine screams "Hey, this class is open to misuse and abuse!"
	public DBDatabase getDatabase() {
		return configuration.getDatabase();
	}

	public String getDefaultUserCaseName() {
		return configuration.getDefaultUserCaseName();
	}
	
	public Connection getConnectionByID(int connectionID) {
		return connections.get(connectionID);
	}

	public String getDefaultLocaleName() {
		return configuration.getDefaultLocaleName();
	}

	public Locale getLocale(String name) {
		return configuration.getLocale(name);
	}
}


