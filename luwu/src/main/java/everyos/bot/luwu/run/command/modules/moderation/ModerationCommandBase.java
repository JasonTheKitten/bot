package everyos.bot.luwu.run.command.modules.moderation;

import java.util.List;
import java.util.function.Function;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ModerationCommandBase<T extends ModerationArguments> extends CommandBase {
	private String moderationName = "command.moderation.unknown";
	private String successMessage = "command.moderation.unknown";
	
	public ModerationCommandBase(String id, Function<Client, Boolean> checkSupportedFunc, int requiredBotPerms, int requiredUserPerms) {
		super(id, checkSupportedFunc, requiredBotPerms, requiredUserPerms);
	}
	
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Channel channel = data.getChannel();
		Locale locale = data.getLocale();
		Member invoker = data.getInvoker();
		
		return
			parseArgs(parser, locale)
			.flatMapMany(args->{
				return Flux.fromArray(args.getUsers())
					.flatMap(uid->channel.getMember(uid)).flatMap(member->{
						return
							ensureAuthority(invoker, member, locale)
							.then(notifyMember(member, locale))
							.then(performAction(args, member, locale))
							.flatMap(result->logResult(result, locale, channel, invoker))
							.onErrorResume(e->Mono.just(new Result(false, member)));
					});
			})
			.onErrorResume(e->{
				if (e instanceof TextException) return Mono.error(e);
				e.printStackTrace();
				return Mono.just(new Result(false, null));
			})
			.collectList()
			.flatMap(list->sendActionSuccess(channel, locale, list));
	}
	
	abstract protected Mono<T> parseArgs(ArgumentParser parser, Locale locale);
	
	protected Mono<Member> ensureAuthority(Member invoker, Member member, Locale locale) {
		//Ensure each member is of lower rank than invoker
		return invoker.isHigherThan(member).flatMap(lt->{
			if (!lt) return Mono.error(new TextException(locale.localize("command.error.hierarchy")));
			return Mono.just(member);
		});
	}
	
	protected Mono<Member> notifyMember(Member member, Locale locale) {
		//Send a message to the user
		if (member.isBot()) {
			return Mono.empty();
		}
		return member.getPrivateChannel()
			.map(pchannel->pchannel.getInterface(ChannelTextInterface.class))
			.flatMap(tchannel->tchannel.send(locale.localize("command.modlog.nosetreason")))
			.onErrorResume(e->{e.printStackTrace(); return Mono.empty();})
			.then(Mono.just(member));
	}
	
	protected abstract Mono<Result> performAction(T arguments, Member member, Locale locale);
	
	protected Mono<Result> logResult(Result result, Locale locale, Channel channel, Member invoker) {
		//Send a mod log message
		return LoggedModerationAction.log(moderationName, channel, invoker, result.getMember(), locale.localize("command.modlog.setreason"))
			.then(Mono.just(result));
	}
	
	protected Mono<Void> sendActionSuccess(Channel channel, Locale locale, List<Result> l) {
		//Indicate that our process has finished
		//TODO: Indicate failures and usernames, and send mod-logs
		ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
		int fails = 0;
		for (Result b:l) {
			if (!b.getSuccess()) fails++;
		}
		return textGrip.send(locale.localize(successMessage, "errors", String.valueOf(fails)))
			.then();
	}
	
	protected class Result {
		private boolean success;
		private Member member;
		
		public Result(boolean success, Member member) {
			this.success = success;
			this.member = member;
		}
		public boolean getSuccess() {
			return success;
		};
		public Member getMember() {
			return member;
		}
	}
}
