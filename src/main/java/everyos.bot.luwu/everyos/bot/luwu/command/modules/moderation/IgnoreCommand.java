package everyos.bot.luwu.command.modules.moderation;

public class IgnoreCommand {
	
}

/*package everyos.bot.luwu.command.modules.moderation;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import everyos.bot.chat4j.enm.ChatPermission;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.bot.luwu.annotation.CommandID;
import everyos.bot.luwu.annotation.Help;
import everyos.bot.luwu.annotation.Permissions;
import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.exception.TextException;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CommandID(id=1)
@Permissions(permissions={ChatPermission.BAN})
@Help(help="command.ban.help", ehelp="command.ban.ehelp", usage="command.ban.usage")
public class BanCommand implements Command {
	private BanCommand() {}
	
	private static BanCommand instance;
	public static BanCommand get() {
		if (instance==null) instance = new BanCommand();
		return instance;
	}
	
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		Channel channel = data.getChannel();
		ChatChannelTextInterface textGrip =  channel.getInterface(ChatChannelTextInterface.class);
		Locale locale = data.getLocale();
		Member invoker = data.getInvoker();
		
		return
			checkPerms(invoker, data.getLocale())
			.then(parseArgs(parser));
		//Does the user have permissions?
		
			
			//Read each of the users that must be banned
			ArrayList<Long> ids = new ArrayList<>();
			while (!parser.isEmpty()) {
				if (!parser.couldBeUserID()) {
					return Mono.error(new TextException(locale.localize("command.error.usage", "expected", locale.localize("user"), "got", "`"+parser.eat()+"`")));
				}
				ids.add(parser.eatUserID());
				
				//We have found a mod-log reason
				if (!parser.isEmpty()&&parser.peek().equals(";")) {
					//TODO: Temp time
					//TODO: Mod log message
					break;
				}
			}
			
			//Prevents us from hitting ratelimits, probably
			if (ids.size()<1) return Mono.error(new TextException(locale.localize("command.error.banmin", "min", "1")));
			if (ids.size()>3) return Mono.error(new TextException(locale.localize("command.error.banmax", "max", "3")));
			
			return Flux.fromArray(ids.toArray(new Long[ids.size()]))
				.flatMap(id->channel.getMember(id));
		}).flatMap(member->private Mono parseArgs(ArgumentParser parser) {
		// TODO Auto-generated method stub
		return null;
	}
		{
			//Ensure each member is of lower rank than invoker
			return invoker.isHigherThan(member).flatMap(lt->{
				if (!lt) return Mono.error(new TextException(locale.localize("command.error.hierarchy")));
				
				return
					//Send a message to the user
					member.getPrivateChannel()
					.map(pchannel->pchannel.getInterface(ChatChannelTextInterface.class))
					.flatMap(tchannel->tchannel.send(locale.localize("command.modlog.nosetreason")))
					.onErrorResume(e->{e.printStackTrace(); return Mono.empty();})
					//Ban the user
					.then(member.getInterface(ChatMemberModerationInterface.class).ban(null))
					//Send a mod log message
					.then(LoggedModerationAction.log("Ban", channel, invoker, member, locale.localize("command.modlog.setreason")))
					//Indicate success
					.then(Mono.just(true));
			}).onErrorResume(e->{
				if (!(e instanceof TextException)) e.printStackTrace();
				//Indicate failure
				return Mono.just(false);
			});
		}).collectList().flatMap(l->{
			//Indicate that our process has finished
			//TODO: Indicate failures and usernames, and send mod-logs
			int fails = 0;
			for (boolean b:l) {
				if (!b) fails++;
			}
			return textGrip.send(locale.localize("command.ban.success", "errors", String.valueOf(fails)));
		}); 
	}

	private Mono<Void> checkPerms(Member invoker, Locale locale) {
		return invoker.hasPermissions(ChatPermission.BAN).flatMap(hasPermission->{
			if (!hasPermission) {
				return Mono.error(new TextException(locale.localize("command.error.perms"))).then();
			}
			return Mono.empty();
		};
	}
}*/