package everyos.bot.luwu.command.modules.moderation;

import java.util.List;

import everyos.bot.chat4j.enm.ChatPermission;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.exception.TextException;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ModerationCommandBase<T extends ModerationArguments> implements Command {
	private String moderationName = "command.moderation.unknown";
	private String successMessage = "command.moderation.unknown";
	private ChatPermission[] permissions;
	
	public ModerationCommandBase(ChatPermission[] permissions) {
		this.permissions = permissions;
	}
	
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		Channel channel = data.getChannel();
		Locale locale = data.getLocale();
		Member invoker = data.getInvoker();
		
		return
			checkPerms(invoker, locale)
			.then(parseArgs(parser, locale))
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
			.doOnError(e->e.printStackTrace())
			.onErrorResume(e->Mono.just(new Result(false, null)))
			.collectList()
			.flatMap(list->sendActionSuccess(channel, locale, list));
	}

	

	protected Mono<Void> checkPerms(Member invoker, Locale locale) {
		//Does the user have permissions?
		return invoker.hasPermissions(permissions).flatMap(hasPermission->{
			if (!hasPermission) {
				return Mono.error(new TextException(locale.localize("command.error.perms"))).then();
			}
			return Mono.empty();
		});
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
		return member.getPrivateChannel()
			.map(pchannel->pchannel.getInterface(ChatChannelTextInterface.class))
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
		ChatChannelTextInterface textGrip = channel.getInterface(ChatChannelTextInterface.class);
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
