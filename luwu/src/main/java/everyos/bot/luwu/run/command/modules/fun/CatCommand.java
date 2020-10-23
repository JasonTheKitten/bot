package everyos.bot.luwu.run.command.modules.fun;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class CatCommand implements Command {
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return data.getChannel().getInterface(ChatChannelTextInterface.class)
			.send(spec->{
				spec.setContent("This command does not exist, but take a cat anyways! (Image from Fandom)"); //TODO: Localize
				spec.addAttachment("cat.gif", "https://vignette.wikia.nocookie.net/cats/images/e/e7/Nyancat.gif/revision/latest?path-prefix=en");
			})
			.then();
	}
}
