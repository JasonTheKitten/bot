package everyos.bot.luwu.run.command.modules.fun;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class CatCommand implements Command {
	static final String URL =
		"https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/67401945-34fc-46b8-8e8f-19828472"+
		"77d4/ddba22b-2fad9d00-1d3f-4ec8-a65d-199a09dfa4e1.gif?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI"+
		"1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvNjc0MDE5N"+
		"DUtMzRmYy00NmI4LThlOGYtMTk4Mjg0NzI3N2Q0XC9kZGJhMjJiLTJmYWQ5ZDAwLTFkM2YtNGVjOC1hNjVkLTE5OWEw"+
		"OWRmYTRlMS5naWYifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ._-whxwEBEaTLWUvSWL80K"+
		"TGiwpoy9dSPzXSRhfTAzeM";
	
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(spec->{
				spec.setContent("This command does not exist, but take a cat anyways! (Image from WixMP)"); //TODO: Localize
				spec.addAttachment("cat.gif", URL);
			})
			.then();
	}
}
