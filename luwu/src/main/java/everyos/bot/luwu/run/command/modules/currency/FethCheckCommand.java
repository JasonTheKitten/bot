package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class FethCheckCommand extends CommandBase {
	public FethCheckCommand() {
		super("command.feth.check", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, data.getInvoker().getID(), locale)
			.flatMap(userID->runCommand(data.getChannel(), userID.getT1(), userID.getT2(), locale));
	}
	
	private Mono<Tuple<UserID, Boolean>> parseArgs(ArgumentParser parser, UserID defaultID, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.just(Tuple.of(defaultID, true));
		}
		
		if (!parser.couldBeUserID()) {
			return expect(locale, parser, "command.error.userid");
		}
		UserID userID = parser.eatUserID();
		boolean isSelf = userID.equals(defaultID);
		
		return Mono.just(Tuple.of(userID, isSelf));
	}

	private Mono<Void> runCommand(Channel channel, UserID target, boolean isSelf, Locale locale) {
		return channel.getServer().flatMap(server->server.as(FethServer.type)).flatMap(server->{
			return server.getCurrencyInfo().flatMap(info->{
				if (!info.getCurrencyEnabled()) {
					return Mono.error(new TextException(locale.localize("command.feth.error.disabled")));
				}
				
				return Mono.just(server);
			});
		}).flatMap(server->{
			return target.getUser()
				.flatMap(user->user.asMemberOf(channel))
				.map(member->member.as(FethMember.type))
				.flatMap(member->{
					return member.getInfo().flatMap(fethState->{
						ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
						return textGrip.send(locale.localize("command.feth.check.message",
							"currency", String.valueOf(fethState.getCurrency()),
							"target", isSelf?"The invoker (you)":member.getHumanReadableID())); //TODO: Localize
					}).then();
				});
		});
	}
}
