package everyos.bot.luwu.run.command.modules.moderation;

import java.util.ArrayList;

import everyos.bot.chat4j.enm.ChatPermission;
import everyos.bot.luwu.core.annotation.Permissions;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.member.MemberModerationInterface;
import everyos.bot.luwu.run.command.modules.moderation.BanCommand.BanArguments;
import reactor.core.publisher.Mono;

@Permissions(permissions={ChatPermission.BAN})
public class BanCommand extends ModerationCommandBase<BanArguments> {
	private BanCommand() {
		super("command.ban", new ChatPermission[] {ChatPermission.BAN});
	}
	
	private static BanCommand instance;
	public static BanCommand get() {
		if (instance==null) instance = new BanCommand();
		return instance;
	}
	
	@Override protected Mono<BanArguments> parseArgs(ArgumentParser parser, Locale locale) {
		//Read each of the users that must be banned
		ArrayList<UserID> ids = new ArrayList<>();
		String reason = null;
		while (!parser.isEmpty()) {
			if (!parser.couldBeUserID()) {
				return Mono.error(new TextException(locale.localize("command.error.usage", "expected", locale.localize("user"), "got", "`"+parser.eat()+"`")));
			}
			ids.add(parser.eatUserID());
			
			//TODO: Temp time
			
			//We have found a mod-log reason
			if (!parser.isEmpty()&&parser.peek(1).equals(";")) {
				parser.eat();
				reason = parser.toString();
				break;
			}
		}
		
		//Prevents us from hitting ratelimits, probably
		if (ids.size()<1) return Mono.error(new TextException(locale.localize("command.error.banmin", "min", "1")));
		if (ids.size()>3) return Mono.error(new TextException(locale.localize("command.error.banmax", "max", "3")));//Read each of the users that must be banned
		
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
	
	@Override protected Mono<Result> performAction(BanArguments arguments, Member member, Locale locale) {
		return member.getInterface(MemberModerationInterface.class).ban(arguments.getReason())
			.then(Mono.just(new Result(true, member)));
	}
	
	static protected interface BanArguments extends ModerationArguments {
		
	}
}
