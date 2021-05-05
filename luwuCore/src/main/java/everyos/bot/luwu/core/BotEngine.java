package everyos.bot.luwu.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.UserCase;
import everyos.bot.luwu.core.database.DBDatabase;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.ClientWrapper;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.event.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotEngine {
	private Client[] clients;
	private BotEngineConfiguration configuration;
	private Map<Integer, Connection> connections;
	private HookBinding<?>[] hooks;
	private TimedTask[] timedTasks;
	
	public BotEngine(BotEngineConfiguration configuration) {
		this.configuration = configuration;
		this.connections = new HashMap<>();
		this.hooks = configuration.getHooks();
		this.timedTasks = configuration.getTimedTasks();
		
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
			}).onErrorResume(e->{
				e.printStackTrace();
				return Mono.empty();
			}));
		}	
		return mono;
	}

	//Creating event handlers
	private Mono<Void> createHandlers(Connection connection) {
		return timedTasks(connection).and(hook(connection, Event.class)).then();
	}

	private <T extends Event> Flux<T> hook(Connection connection, Class<T> eventClass) {
		Flux<T> eventFlux = connection.generateEventListener(eventClass);
		if (eventFlux == null) return Flux.empty();
		return eventFlux.flatMap(event->{
			try {
				Mono<Void> m1 = Mono.empty();
				for (HookBinding<?> hook: hooks) {
					try {
						m1 = hook.apply(m1, event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return m1
					.onErrorResume(e->{
						e.printStackTrace();
						return Mono.empty();
					})
					.then(Mono.just(event));
			} catch (Exception e) {
				e.printStackTrace();
				return Mono.empty();
			}
		});
	}
	
	private Mono<Void> timedTasks(Connection connection) {
		Mono<Void> m1 = Mono.empty();
		
		for (TimedTask m2: timedTasks) {
			m1 = m2.apply(connection, m1);
		}
		
		return m1;
	}

	public Mono<ChannelCase> getChannelCase(CommandData data) {
		return Mono.defer(()->{
			Mono<String> channelCaseNameMono = data.getChannel().getType();
			for (BiFunction<Mono<String>, CommandData, Mono<String>> func: configuration.getChannelCaseTransformers()) {
				channelCaseNameMono = func.apply(channelCaseNameMono, data);
			}
			return channelCaseNameMono.map(name->configuration.getChannelCase(name));
		});
	}
	
	public Mono<UserCase> getUserCase(CommandData data) {
		return Mono.defer(()->{
			// TODO: Look up the user case in database, and only use default if not present
			Mono<String> userCaseNameMono = Mono.just(configuration.getDefaultUserCaseName());
			for (BiFunction<Mono<String>, CommandData, Mono<String>> func: configuration.getUserCaseTransformers()) {
				userCaseNameMono = func.apply(userCaseNameMono, data);
			}
			return userCaseNameMono.map(name->configuration.getUserCase(name));
		});
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

	//TODO: Should this exist?
	@Deprecated
	public Configuration getConfiguration() {
		return configuration.getConfiguration();
	}
}