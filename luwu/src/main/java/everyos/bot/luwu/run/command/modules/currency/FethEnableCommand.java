package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class FethEnableCommand extends CommandBase {
	private boolean enable;

	public FethEnableCommand(boolean enable) {
		super(enable?"command.feth.enable":"command.feth.disable", e->true,
			ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_GUILD);
		this.enable = enable;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return 
			data.getInvoker().getServer().flatMap(server->server.as(FethServer.type)).flatMap(server->{
				return server.getCurrencyInfo().flatMap(info->{
					if (info.getCurrencyEnabled()==enable) {
						return Mono.error(new TextException(
							locale.localize(enable?
								"command.feth.error.aenabled":
								"command.feth.error.adisabled")));
					}
					
					return server.setCurrencyInfo(new FethServerInfo(enable));
				});
			})
			.then(data.getChannel().getInterface(ChannelTextInterface.class).send(
				locale.localize(enable?
					"command.feth.enabled":
					"command.feth.disabled")))
			.then();
	}
}
