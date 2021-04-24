package everyos.bot.luwu.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.UserCase;
import everyos.bot.luwu.core.database.DBDatabase;
import everyos.bot.luwu.core.entity.ClientWrapper;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.event.Event;
import reactor.core.publisher.Mono;

public class BotEngineBuilder {
	private List<ClientWrapper> clients = new ArrayList<>();
	
	private Map<String, ChannelCase> channelCases = new HashMap<>();
	private String defaultChannelCase;
	private List<BiFunction<Mono<String>, CommandData, Mono<String>>> channelCaseTransformers = new ArrayList<>();
	
	private Map<String, UserCase> userCases = new HashMap<>();
	private String defaultUserCase;
	private List<BiFunction<Mono<String>, CommandData, Mono<String>>> userCaseTransformers = new ArrayList<>();
		
	private DBDatabase database;
	
	private String defaultLocale;
	private Map<String, Locale> locales = new HashMap<>();
	
	private List<HookBinding<?>> hookBindings = new ArrayList<>();
	private List<TimedTask> timedTasks = new ArrayList<>();

	private Configuration globalConfiguration; //TODO: Should not exist

	public BotEngine build() {	
		final ClientWrapper[] clients = this.clients.toArray(new ClientWrapper[this.clients.size()]);
		
		@SuppressWarnings("unchecked")
		BiFunction<Mono<String>, CommandData, Mono<String>>[] userCaseTransformersArray =
			userCaseTransformers.toArray(new BiFunction[userCaseTransformers.size()]);
		
		BotEngineConfiguration configuration = new BotEngineConfiguration() {
			@Override public ClientWrapper[] getClients() {
				return clients;
			}

			@Override public String getDefaultUserCaseName() {
				return defaultUserCase;
			}
			@Override public BiFunction<Mono<String>, CommandData, Mono<String>>[] getUserCaseTransformers() {
				return userCaseTransformersArray;
			}
			@Override public UserCase getUserCase(String name) {
				return userCases.get(name);
			}
			
			//TODO: Under DRY principles, perhaps I should just make a class for this?
			@Override
			public String getDefaultChannelCaseName() {
				return defaultChannelCase;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public BiFunction<Mono<String>, CommandData, Mono<String>>[] getChannelCaseTransformers() {
				return channelCaseTransformers.toArray(new BiFunction[channelCaseTransformers.size()]);
			}
			
			@Override
			public ChannelCase getChannelCase(String name) {
				return channelCases.get(name);
			}

			@Override
			public DBDatabase getDatabase() {
				return database;
			}

			@Override
			public String getDefaultLocaleName() {
				return defaultLocale;
			}
			@Override
			public Locale getLocale(String name) {
				return locales.get(name);
			}
			
			@Override
			public HookBinding<?>[] getHooks() {
				return hookBindings.toArray(new HookBinding[hookBindings.size()]);
			}
			
			@Override
			public TimedTask[] getTimedTasks() {
				return timedTasks.toArray(new TimedTask[timedTasks.size()]);
			}
			
			@Override
			public Configuration getConfiguration() {
				return globalConfiguration;
			}
		};
		
		return new BotEngine(configuration);
	}

	public void registerClient(ClientWrapper clientWrapper) {
		clients.add(clientWrapper);
	}

	public void setDatabase(DBDatabase database) {
		this.database = database;
	}

	public void setWebImplementation(Object object) {
		
	}

	public void setLogger(Object object) {
		
	}

	public void registerLanguage(String language, Locale locale) {
		locales.put(language, locale);
	}

	public void setDefaultLanguage(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public void setDefaultStatus(String string) {
		
	}

	public void registerChannelCase(String caseName, ChannelCase channelCase) {
		channelCases.put(caseName, channelCase);
	}
	public void setDefaultChannelCase(String defaultChannelCase) {
		this.defaultChannelCase = defaultChannelCase;
	}
	public void transformChannelCase(BiFunction<Mono<String>, CommandData, Mono<String>> transformer) {
		this.channelCaseTransformers.add(transformer);
	}

	public void registerUserCase(String caseName, UserCase userCase) {
		userCases.put(caseName, userCase);
	}
	public void setDefaultUserCase(String defaultUserCase) {
		this.defaultUserCase = defaultUserCase;
	}
	public void transformUserCase(BiFunction<Mono<String>, CommandData, Mono<String>> transformer) {
		this.userCaseTransformers.add(transformer);
	}

	public <T extends Event> void registerHook(Class<T> event, Function<T, Mono<Void>> func) {
		hookBindings.add(new HookBinding<T>(false, event, func));
	}
	public <T extends Event> void registerSequencedHook(Class<T> event, Function<T, Mono<Void>> func) {
		hookBindings.add(new HookBinding<T>(true, event, func));
	}

	public void registerRepeatingTask(long period, Function<Connection, Mono<Void>> func) {
		timedTasks.add(new TimedTask(period, func));
    }

	// TODO: Should Not Exist
	public void setConfiguration(Configuration configuration) {
		this.globalConfiguration = configuration;
	}
}
