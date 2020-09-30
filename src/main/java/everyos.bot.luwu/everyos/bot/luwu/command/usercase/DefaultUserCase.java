package everyos.bot.luwu.command.usercase;

import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.command.channelcase.DefaultChannelCase;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class DefaultUserCase implements Command {
	private static DefaultUserCase instance;
	
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		//Nothing special, we just delegate the task to the proper channel
		return DefaultChannelCase.get().execute(data, parser);
	}
	
	public static DefaultUserCase get() {
		if (instance==null) instance = new DefaultUserCase();
		return instance;
	}
}
