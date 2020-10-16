package everyos.bot.luwu.run.command.modules.moderation;

import java.util.ArrayList;

import everyos.bot.chat4j.enm.ChatPermission;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.bot.luwu.core.annotation.CommandID;
import everyos.bot.luwu.core.annotation.Help;
import everyos.bot.luwu.core.annotation.Permissions;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.run.command.modules.moderation.KickCommand.KickArguments;
import reactor.core.publisher.Mono;

@CommandID(id=1)
@Permissions(permissions={ChatPermission.BAN})
@Help(help="command.kick.help", ehelp="command.kick.ehelp", usage="command.kick.usage")
public class KickCommand extends ModerationCommandBase<KickArguments> {
	private KickCommand() {
		super(new ChatPermission[] {ChatPermission.KICK});
	}
	
	private static KickCommand instance;
	public static KickCommand get() {
		if (instance==null) instance = new KickCommand();
		return instance;
	}
	
	@Override protected Mono<KickArguments> parseArgs(ArgumentParser parser, Locale locale) {
		//Read each of the users that must be kicked
		ArrayList<Long> ids = new ArrayList<>();
		String reason = null;
		while (!parser.isEmpty()) {
			if (!parser.couldBeUserID()) {
				return Mono.error(new TextException(locale.localize("command.error.usage", "expected", locale.localize("user"), "got", "`"+parser.eat()+"`")));
			}
			ids.add(parser.eatUserID());
			
			//TODO: Temp time
			
			//We have found a mod-log reason
			if (!parser.isEmpty()&&parser.peek().equals(";")) {
				parser.eat();
				reason = parser.toString();
				break;
			}
		}
		
		//Prevents us from hitting ratelimits, probably
		if (ids.size()<1) return Mono.error(new TextException(locale.localize("command.error.kickmin", "min", "1")));
		if (ids.size()>3) return Mono.error(new TextException(locale.localize("command.error.kickmax", "max", "3")));//Read each of the users that must be kickned
		
		String freason = reason;
		return Mono.just(new KickArguments() {
			@Override public Long[] getUsers() {
				return ids.toArray(new Long[ids.size()]);
			}

			@Override public String getReason() {
				return freason;
			}
		});
	}
	
	@Override protected Mono<Result> performAction(KickArguments arguments, Member member, Locale locale) {
		return member.getInterface(ChatMemberModerationInterface.class).kick(arguments.getReason())
			.then(Mono.just(new Result(true, member)));
	}
	
	static protected interface KickArguments extends ModerationArguments {
		
	}
}
