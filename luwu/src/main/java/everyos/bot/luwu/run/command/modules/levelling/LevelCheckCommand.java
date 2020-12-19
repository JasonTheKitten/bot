package everyos.bot.luwu.run.command.modules.levelling;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class LevelCheckCommand implements Command {
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//TODO: Is the guild even opted into leveling?
		LevelMember member = data.getInvoker().getWithExtension(LevelMember.type);
		return member.getLevelState().flatMap(levelState->{
			ChannelTextInterface textGrip = data.getChannel().getInterface(ChannelTextInterface.class);
			return textGrip.send("Your total XP is "+String.valueOf(levelState.getXPTotal()));
		}).then();
		
		//TODO: Test with return null
	}
}
