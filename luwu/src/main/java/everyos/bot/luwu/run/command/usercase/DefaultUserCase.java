package everyos.bot.luwu.run.command.usercase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.UserCase;
import reactor.core.publisher.Mono;

public class DefaultUserCase extends UserCase {
	private static DefaultUserCase instance;
	
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//Nothing special, we just delegate the task to the proper channel
		return
			data.getBotEngine().getChannelCase(data)
			.flatMap(channelCase->channelCase.execute(data, parser));
	}
	
	public static DefaultUserCase get() {
		if (instance==null) instance = new DefaultUserCase();
		return instance;
	}
}
