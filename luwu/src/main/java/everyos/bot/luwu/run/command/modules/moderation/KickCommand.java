package everyos.bot.luwu.run.command.modules.moderation;

import java.util.ArrayList;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.member.MemberModerationInterface;
import everyos.bot.luwu.run.command.modules.moderation.KickCommand.KickArguments;
import reactor.core.publisher.Mono;

public class KickCommand extends ModerationCommandBase<KickArguments> {
	private KickCommand() {
		super("command.kick", e->true, ChatPermission.SEND_EMBEDS|ChatPermission.KICK_MEMBERS, ChatPermission.KICK_MEMBERS);
	}
	
	private static KickCommand instance;
	public static KickCommand get() {
		if (instance==null) instance = new KickCommand();
		return instance;
	}
	
	@Override protected Mono<KickArguments> parseArgs(ArgumentParser parser, Locale locale) {
		//Read each of the users that must be kicked
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
		if (ids.size()<1) return Mono.error(new TextException(locale.localize("command.error.kickmin", "min", "1")));
		if (ids.size()>3) return Mono.error(new TextException(locale.localize("command.error.kickmax", "max", "3")));
		
		String freason = reason;
		return Mono.just(new KickArguments() {
			@Override public UserID[] getUsers() {
				return ids.toArray(new UserID[ids.size()]);
			}

			@Override public String getReason() {
				return freason;
			}
		});
	}
	
	@Override protected Mono<Result> performAction(KickArguments arguments, Member member, Locale locale) {
		return member.getInterface(MemberModerationInterface.class).kick(arguments.getReason())
			.then(Mono.just(new Result(true, member)));
	}
	
	static protected interface KickArguments extends ModerationArguments {
		
	}
}
