package everyos.bot.luwu.run.command.modules.leveling;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class LevelEnableCommand extends CommandBase {
	private boolean enable;

	public LevelEnableCommand(boolean enable) {
		super(enable?"command.level.enable":"command.level.disable", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_GUILD);
		this.enable = enable;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return 
			data.getInvoker().getServer().map(server->server.getWithExtension(LevelServer.type)).flatMap(server->{
				return server.getLevelInfo().flatMap(info->{
					if (info.getLevellingEnabled()==enable) {
						return Mono.error(new TextException(
							locale.localize(enable?
								"command.level.error.aenabled":
								"command.level.error.adisabled")));
					}
					
					return server.setLevelInfo(new LevelInfo(enable, info.getMessageChannelID(), info.getLevelMessage()));
				});
			})
			.then(data.getChannel().getInterface(ChannelTextInterface.class).send(
				locale.localize(enable?
					"command.level.enabled":
					"command.level.disabled")))
			.then();
	}
}
