package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class FethDailyCommand extends CommandBase {
	private static final long DAILY_AMOUNT = 20;
	private static final long COOLDOWN_MILLIS = 1000*60*60*24;
	
	public FethDailyCommand() {
		super("command.feth.daily", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), data.getInvoker(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Member invoker, Locale locale) {
		FethMember invokerAs = invoker.as(FethMember.type);
		return 
			invokerAs.edit(spec->{
				FethMemberInfo info = spec.getInfo();
				if (System.currentTimeMillis()-info.getCooldown()<COOLDOWN_MILLIS) {
					return Mono.error(new TextException(locale.localize("command.feth.daily.cooldown")));
				}
				spec.setCurrency(info.getCurrency()+DAILY_AMOUNT);
				spec.setCooldown(System.currentTimeMillis());
				
				return Mono.empty();
			})
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.feth.daily.message")))
			.then();
	}
}
