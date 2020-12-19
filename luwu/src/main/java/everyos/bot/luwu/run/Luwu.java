package everyos.bot.luwu.run;

import java.util.Optional;

import ch.qos.logback.classic.Logger;
import everyos.bot.luwu.core.BotEngineBuilder;
import everyos.bot.luwu.core.Configuration;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import everyos.bot.luwu.discord.DiscordClientBuilder;
import everyos.bot.luwu.language.ResourceLocale;
import everyos.bot.luwu.mongo.MongoDatabaseBuilder;
import everyos.bot.luwu.nertivia.NertiviaClientBuilder;
import everyos.bot.luwu.run.command.channelcase.DefaultChannelCase;
import everyos.bot.luwu.run.command.channelcase.PrivateChannelCase;
import everyos.bot.luwu.run.command.modules.chatlink.channelcase.ChatLinkChannelCase;
import everyos.bot.luwu.run.command.modules.levelling.LevelHooks;
import everyos.bot.luwu.run.command.usercase.DefaultUserCase;
import reactor.core.publisher.Mono;

public class Luwu {
	private static final String PRIVATE_CHANNELCASE = "private";
	private static final String DEFAULT_CHANNELCASE = "default";
	private static final String CHATLINK_CHANNELCASE = "chatlink"; //TODO: The value of this constant is hard-coded elsewhere :/
	
	private static final String DEFAULT_USERCASE = "default";
	
	private static final String ENGLISH_LANGUAGE = "en_US";
	private static final String TURKISH_LANGUAGE = "tr_TR";
	
	public Mono<Void> execute(Configuration configuration, Logger logger) {
		//TODO: Custom logging interfaces
		
		BotEngineBuilder engineBuilder = new BotEngineBuilder();
		
		// Register database
		MongoDatabaseBuilder dbBuilder = new MongoDatabaseBuilder();
		dbBuilder.setURL(configuration.getDatabaseURL());
		dbBuilder.setPassword(configuration.getDatabasePassword());
		dbBuilder.setDBName(configuration.getDatabaseName());
		engineBuilder.setDatabase(dbBuilder.build());
		
		// Register clients (Nertivia and Discord)
		Optional<String> nertiviaToken = configuration.getNertiviaToken();
		if (nertiviaToken.isPresent()) {
			NertiviaClientBuilder nertiviaBuilder = new NertiviaClientBuilder();
			nertiviaBuilder.setToken(nertiviaToken.get());
			engineBuilder.registerClient(nertiviaBuilder.build(1, "n"));
		}
		
		Optional<String> discordToken = configuration.getDiscordToken();
		if (discordToken.isPresent()) {
			DiscordClientBuilder discordBuilder = new DiscordClientBuilder();
			discordBuilder.setToken(discordToken.get());
			engineBuilder.registerClient(discordBuilder.build(0, "d"));
		}
		
		// Misc (TODO)
		engineBuilder.setLogger(null);
		engineBuilder.setWebImplementation(null);
		
		// Register languages
		engineBuilder.registerLanguage(ENGLISH_LANGUAGE, new ResourceLocale("language/en_US.json"));
		engineBuilder.registerLanguage(TURKISH_LANGUAGE, new ResourceLocale("language/tr_TR.json"));
		engineBuilder.setDefaultLanguage(ENGLISH_LANGUAGE);
		
		// Set the bot's status
		engineBuilder.setDefaultStatus("Playing sleepyhead");
		engineBuilder.setServerCountStatus("Watching ${server} servers | luwu help | Luwu!"); //TODO
		
		// Register channel behaviours
		engineBuilder.registerChannelCase(DEFAULT_CHANNELCASE, DefaultChannelCase.get());
		engineBuilder.registerChannelCase(CHATLINK_CHANNELCASE, ChatLinkChannelCase.get());
		engineBuilder.registerChannelCase(PRIVATE_CHANNELCASE, PrivateChannelCase.get());
		engineBuilder.setDefaultChannelCase(DEFAULT_CHANNELCASE);
		engineBuilder.transformChannelCase((type, data)->{ //TODO
			if (data.getChannel().isPrivateChannel()) {
				return Mono.just(PRIVATE_CHANNELCASE);
			}
			return type;
		});
		
		// Register user behaviours
		engineBuilder.registerUserCase(DEFAULT_USERCASE, DefaultUserCase.get());
		engineBuilder.setDefaultUserCase(DEFAULT_USERCASE);
		
		// Register hooks
		engineBuilder.registerHook(MessageCreateEvent.class, LevelHooks::levelHook);
		
		// Start the bot
		return engineBuilder.build().start();
	}
}
