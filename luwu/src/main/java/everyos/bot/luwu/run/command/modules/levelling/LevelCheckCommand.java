package everyos.bot.luwu.run.command.modules.levelling;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class LevelCheckCommand implements Command {
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return data.getInvoker().getServer().map(server->server.getWithExtension(LevelServer.type)).flatMap(server->{
			return server.getLevelInfo().flatMap(info->{
				if (!info.getLevellingEnabled()) {
					return Mono.error(new TextException(locale.localize("command.level.error.disabled")));
				}
				
				return Mono.just(server);
			});
		}).flatMap(server->{
			LevelMember member = data.getInvoker().getWithExtension(LevelMember.type);
			return member.getLevelState().flatMap(levelState->{
				ChannelTextInterface textGrip = data.getChannel().getInterface(ChannelTextInterface.class);
				return textGrip.send(
					"Current Level: "+String.valueOf(levelState.getLevel())+"\n"+
					"XP: "+levelState.getXPTotal()+" ("+String.valueOf(levelState.getXPLeveled())+"/"+String.valueOf(levelState.getXPToNextLevel())+")");
						//TODO: Embed and localize
			}).then();
		});
		//TODO: Test error handling with `return null;` instead of `return Mono.empty();`
	}
}
