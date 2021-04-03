package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class FethGiveCommand extends CommandBase {
	public FethGiveCommand() {
		//TODO: Don't allow when feth is disabled
		super("command.feth.give", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, data.getInvoker().getID(), locale)
			.flatMap(tup->runCommand(data.getChannel(), data.getInvoker(), tup.getT1(), tup.getT2(), locale));
	}

	private Mono<Tuple<UserID, Long>> parseArgs(ArgumentParser parser, UserID id, Locale locale) {
		if (!parser.couldBeUserID()) {
			return expect(locale, parser, "command.error.userid");
		}
		UserID target = parser.eatUserID();
		if (target.equals(id)) {
			return Mono.error(new TextException(locale.localize("command.feth.give.giveself")));
		}
		
		if (!parser.isNumerical() || parser.peek().startsWith("-")) {
			return expect(locale, parser, "command.error.positiveinteger");
		}
		long transfer = parser.eatNumerical();
		
		return Mono.just(Tuple.of(target, transfer));
	}

	private Mono<Void> runCommand(Channel channel, Member invoker, UserID target, long amount, Locale locale) {
		FethMember invokerAs = invoker.as(FethMember.type);
		return invokerAs.getInfo().flatMap(info->{
			if (info.getCurrency()<amount) {
				return Mono.error(new TextException("command.feth.give.notenough"));
			}
			return target.getUser()
				.flatMap(user->user.asMemberOf(channel))
				.map(user->user.as(FethMember.type))
				.flatMap(member->{
					if (member.isBot()) {
						return Mono.error(new TextException("command.feth.give.isbot"));
					}
					
					return member.edit(spec->{
						spec.setCurrency(spec.getInfo().getCurrency()+amount);
						return Mono.empty();
					}).and(invokerAs.edit(spec->{
						spec.setCurrency(spec.getInfo().getCurrency()-amount);
						return Mono.empty();
					}));
				});
		});
	}
}
