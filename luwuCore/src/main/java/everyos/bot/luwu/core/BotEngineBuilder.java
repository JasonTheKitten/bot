package everyos.bot.luwu.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.UserCase;
import everyos.bot.luwu.core.database.DBDatabase;
import everyos.bot.luwu.core.entity.ClientWrapper;
import reactor.core.publisher.Mono;

public class BotEngineBuilder {
	private ArrayList<ClientWrapper> clients = new ArrayList<>();
	
	private HashMap<String, ChannelCase> channelCases = new HashMap<>();
	private String defaultChannelCase;
	private ArrayList<BiFunction<Mono<String>, CommandData, Mono<String>>> channelCaseTransformers = new ArrayList<>();
	
	private HashMap<String, UserCase> userCases = new HashMap<>();
	private String defaultUserCase;
	private ArrayList<BiFunction<Mono<String>, CommandData, Mono<String>>> userCaseTransformers = new ArrayList<>();

	@SuppressWarnings("unused")
	private DBDatabase database;
	
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
			@Override public String getDefaultChannelCaseName() {
				return defaultChannelCase;
			}
			@SuppressWarnings("unchecked")
			@Override public BiFunction<Mono<String>, CommandData, Mono<String>>[] getChannelCaseTransformers() {
				return channelCaseTransformers.toArray(new BiFunction[channelCaseTransformers.size()]);
			}
			@Override public ChannelCase getChannelCase(String name) {
				return channelCases.get(name);
			}

			@Override public DBDatabase getDatabase() {
				return database;
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

	public void registerLanguage(String englishLanguage, Object object) {
		
	}

	public void setDefaultLanguage(String englishLanguage) {
		
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

	public void setServerCountStatus(String string) {
		
	}
}
