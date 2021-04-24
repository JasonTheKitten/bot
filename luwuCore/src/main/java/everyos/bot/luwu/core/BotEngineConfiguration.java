package everyos.bot.luwu.core;

import java.util.function.BiFunction;

import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.UserCase;
import everyos.bot.luwu.core.database.DBDatabase;
import everyos.bot.luwu.core.entity.ClientWrapper;
import everyos.bot.luwu.core.entity.Locale;
import reactor.core.publisher.Mono;

public interface BotEngineConfiguration {
	ClientWrapper[] getClients();
	
	String getDefaultUserCaseName();
	BiFunction<Mono<String>, CommandData, Mono<String>>[] getUserCaseTransformers();
	UserCase getUserCase(String name);

	String getDefaultChannelCaseName();
	BiFunction<Mono<String>, CommandData, Mono<String>>[] getChannelCaseTransformers();
	ChannelCase getChannelCase(String name);

	DBDatabase getDatabase();

	String getDefaultLocaleName();
	Locale getLocale(String name);

	HookBinding<?>[] getHooks();
	TimedTask[] getTimedTasks();

	// Should not exist
	Configuration getConfiguration();
}
