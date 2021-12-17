package everyos.bot.luwu.run.command.modules.leveling;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class LevelCheckCommand extends CommandBase {
	
	public LevelCheckCommand() {
		super("command.level.check", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return data.getInvoker().getServer().flatMap(server->server.as(LevelServer.type)).flatMap(server->{
			return server.getLevelInfo().flatMap(info->{
				if (!info.getLevellingEnabled()) {
					return Mono.error(new TextException(locale.localize("command.level.error.disabled")));
				}
				
				return Mono.just(server);
			});
		}).flatMap(server->{
			LevelMember member = data.getInvoker().as(LevelMember.type);
			return member.getLevelState().flatMap(levelState->{
				ChannelTextInterface textGrip = data.getChannel().getInterface(ChannelTextInterface.class);
				return textGrip.send(
					"Current Level: "+String.valueOf(levelState.getLevel())+"\n"+
					"XP: "+levelState.getXPTotal()+" ("+String.valueOf(levelState.getXPLeveled())+"/"+String.valueOf(levelState.getXPToNextLevel())+")");
			}).then();
		});
		//TODO: Localize
		//TODO: Should I use an embed?
		//TODO: Test error handling with `return null;` instead of `return Mono.empty();`
	}
	
}
