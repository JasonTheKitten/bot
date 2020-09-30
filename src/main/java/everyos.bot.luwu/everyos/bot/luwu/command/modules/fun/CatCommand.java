package everyos.bot.luwu.command.modules.fun;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class CatCommand implements Command {
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		data.getChannel().getInterface(ChatChannelTextInterface.class)
			.send(spec->{
				spec.setContent("This command does not exist, but take a cat anyways! (Image from Fandom)"); //TODO: Localize
				spec.addImage("https://vignette.wikia.nocookie.net/cats/images/e/e7/Nyancat.gif/revision/latest?path-prefix=en");
			});
		return null;
	}
}
