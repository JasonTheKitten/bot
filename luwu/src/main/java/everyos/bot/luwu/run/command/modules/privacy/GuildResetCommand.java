package everyos.bot.luwu.run.command.modules.privacy;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class GuildResetCommand extends CommandBase {

	public GuildResetCommand() {
		super("command.resetguild", e->true,
			ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_GUILD);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArguments(parser, locale)
			.then(runCommand(data.getChannel(), locale));
	}

	private Mono<Void> parseArguments(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty() && !(parser.peek().equals("-f") || parser.peek().equals("--force"))) {
			return Mono.error(new TextException(locale.localize("command.resetguild.nforce")));
		}
		return Mono.empty();
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.wipe())
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.resetguild.message")))
			.then();
	}
}
