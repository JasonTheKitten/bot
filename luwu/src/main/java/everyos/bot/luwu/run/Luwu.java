package everyos.bot.luwu.run;

import java.util.Optional;

import org.slf4j.Logger;

import everyos.bot.luwu.core.BotEngineBuilder;
import everyos.bot.luwu.core.Configuration;
import everyos.bot.luwu.core.entity.event.MemberEvent;
import everyos.bot.luwu.core.entity.event.MemberJoinEvent;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import everyos.bot.luwu.core.entity.event.MessageEvent;
import everyos.bot.luwu.core.entity.event.ReactionEvent;
import everyos.bot.luwu.core.entity.event.ServerDeleteEvent;
import everyos.bot.luwu.core.entity.event.ServerEvent;
import everyos.bot.luwu.core.event.MessageCreateEventProcessor;
import everyos.bot.luwu.discord.DiscordClientBuilder;
import everyos.bot.luwu.language.ResourceLocale;
import everyos.bot.luwu.mongo.MongoDatabaseBuilder;
import everyos.bot.luwu.nertivia.NertiviaClientBuilder;
import everyos.bot.luwu.run.command.channelcase.DefaultChannelCase;
import everyos.bot.luwu.run.command.channelcase.PrivateChannelCase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannelCase;
import everyos.bot.luwu.run.command.modules.currency.FethHooks;
import everyos.bot.luwu.run.command.modules.leveling.LevelHooks;
import everyos.bot.luwu.run.command.modules.logging.LogsHooks;
import everyos.bot.luwu.run.command.modules.oneword.OneWordChannelCase;
import everyos.bot.luwu.run.command.modules.privacy.PrivacyHooks;
import everyos.bot.luwu.run.command.modules.role.autorole.AutoroleHooks;
import everyos.bot.luwu.run.command.modules.role.reaction.ReactionHooks;
import everyos.bot.luwu.run.command.modules.starboard.StarboardHooks;
import everyos.bot.luwu.run.command.modules.suggestions.SuggestionChannelCase;
import everyos.bot.luwu.run.command.modules.tickets.channel.TicketChannelCase;
import everyos.bot.luwu.run.command.modules.welcome.WelcomeHooks;
import everyos.bot.luwu.run.command.usercase.DefaultUserCase;
import everyos.bot.luwu.run.status.StatusHooks;
import reactor.core.publisher.Mono;

/*
 * ISSUE BOARD 
 * Port some moderation commands
 * Port playlists (started)
 * Quality Assurance
 */

public class Luwu {
	
	private static final String PRIVATE_CHANNELCASE = "private";
	private static final String DEFAULT_CHANNELCASE = "default";
	private static final String CHATLINK_CHANNELCASE = "chatlink"; //TODO: The value of this constant is hard-coded elsewhere :/
	private static final String ONEWORD_CHANNELCASE = "oneword";
	private static final String SUGGESTIONS_CHANNELCASE = "suggestions";
	private static final String TICKET_CHANNELCASE = "ticket";
	
	private static final String DEFAULT_USERCASE = "default";
	
	private static final String ENGLISH_LANGUAGE = "en_US";
	private static final String TURKISH_LANGUAGE = "tr_TR";
	
	public Mono<Void> execute(Configuration configuration, Logger logger) {
		//TODO: Custom logging interfaces
		
		BotEngineBuilder engineBuilder = new BotEngineBuilder();
		
		// Stuff that should not exist
		engineBuilder.setConfiguration(configuration);
		
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
			//engineBuilder.registerClient(nertiviaBuilder.build(1, "n"));
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
		
		// Register channel behaviors
		engineBuilder.registerChannelCase(DEFAULT_CHANNELCASE, DefaultChannelCase.get());
		engineBuilder.registerChannelCase(CHATLINK_CHANNELCASE, LinkChannelCase.get());
		engineBuilder.registerChannelCase(PRIVATE_CHANNELCASE, PrivateChannelCase.get());
		engineBuilder.registerChannelCase(ONEWORD_CHANNELCASE, OneWordChannelCase.get());
		engineBuilder.registerChannelCase(SUGGESTIONS_CHANNELCASE, SuggestionChannelCase.get());
		engineBuilder.registerChannelCase(TICKET_CHANNELCASE, TicketChannelCase.get());
		engineBuilder.setDefaultChannelCase(DEFAULT_CHANNELCASE);
		engineBuilder.transformChannelCase((type, data)->{
			if (data.getChannel().isPrivateChannel()) {
				return Mono.just(PRIVATE_CHANNELCASE);
			}
			return type;
		});
		
		// Register user behaviors
		engineBuilder.registerUserCase(DEFAULT_USERCASE, DefaultUserCase.get());
		engineBuilder.setDefaultUserCase(DEFAULT_USERCASE);
		
		// Register hooks
		engineBuilder.registerHook(MessageCreateEvent.class, LevelHooks::levelHook);
		engineBuilder.registerHook(ReactionEvent.class, ReactionHooks::reactionHook);
		engineBuilder.registerHook(MemberJoinEvent.class, AutoroleHooks::autoroleHook);
		engineBuilder.registerHook(MemberEvent.class, WelcomeHooks::welcomeHook);
		engineBuilder.registerHook(ReactionEvent.class, StarboardHooks::starboardHook);
		engineBuilder.registerHook(ServerEvent.class, StatusHooks::statusHook);
		engineBuilder.registerHook(MessageEvent.class, LogsHooks::logsHook);
		engineBuilder.registerHook(MessageCreateEvent.class, FethHooks::fethHook);
		engineBuilder.registerHook(ServerDeleteEvent.class, PrivacyHooks::privacyHook);
		engineBuilder.registerSequencedHook(MessageCreateEvent.class, MessageCreateEventProcessor::apply);

		//Register repeating tasks
		engineBuilder.registerRepeatingTask(60000, StatusHooks::statusUpdateHook);
		
		// Start the bot
		return engineBuilder.build().start();
	}
	
}
