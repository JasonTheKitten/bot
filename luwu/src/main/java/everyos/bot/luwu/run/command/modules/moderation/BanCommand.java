package everyos.bot.luwu.run.command.modules.moderation;

import java.util.ArrayList;
import java.util.List;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.member.MemberModerationInterface;
import everyos.bot.luwu.run.command.modules.moderation.BanCommand.BanArguments;
import reactor.core.publisher.Mono;

public class BanCommand extends ModerationCommandBase<BanArguments> {
	
	private BanCommand() {
		super("command.ban", e->true,
			ChatPermission.SEND_EMBEDS | ChatPermission.BAN_MEMBERS,
			ChatPermission.BAN_MEMBERS);
	}
	
	@Override
	protected Mono<BanArguments> parseArguments(ArgumentParser parser, Locale locale) {
		//Read each of the users that must be banned
		List<UserID> ids = new ArrayList<>();
		String reason = null;
		while (!parser.isEmpty()) {
			if (!parser.couldBeUserID()) {
				return expect(locale, parser, locale.localize("user"));
			}
			ids.add(parser.eatUserID());
			
			//TODO: Temp time
			
			//We have found a mod-log reason
			if (!parser.isEmpty() && parser.peek(1).equals(";")) {
				parser.eat();
				reason = parser.toString();
				break;
			}
		}
		
		//Prevents us from hitting ratelimits, probably
		if (ids.size() < 1) {
			return Mono.error(new TextException(locale.localize("command.error.banmin", "min", "1")));
		}
		if (ids.size() > 3) {
			return Mono.error(new TextException(locale.localize("command.error.banmax", "max", "3")));
		}
		
		String freason = reason;
		return Mono.just(new BanArguments() {
			@Override public UserID[] getUsers() {
				return ids.toArray(new UserID[ids.size()]);
			}

			@Override public String getReason() {
				return freason;
			}
		});
	}
	
	@Override
	protected Mono<Result> performAction(BanArguments arguments, Member member, Locale locale) {
		return member.getInterface(MemberModerationInterface.class).ban(arguments.getReason())
			.then(Mono.just(new Result(true, member)));
	}
	
	static protected interface BanArguments extends ModerationArguments {
		
	}
	
	private static BanCommand instance = new BanCommand();
	public static BanCommand get() {
		return instance;
	}
	
}
