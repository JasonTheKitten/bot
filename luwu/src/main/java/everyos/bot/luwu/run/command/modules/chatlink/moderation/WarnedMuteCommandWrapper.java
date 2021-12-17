package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.modules.moderation.MuteCommand;
import reactor.core.publisher.Mono;

public class WarnedMuteCommandWrapper implements Command {
	
	private MuteCommand muteCommand;
	
	public WarnedMuteCommandWrapper(boolean isMute) {
		muteCommand = new MuteCommand(isMute);
	}
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return 
			data.getChannel().getInterface(ChannelTextInterface.class)
				.send(data.getLocale().localize("command.link.mute.warned.warning"))
			.then(muteCommand.run(data, parser));
	}
	
	@Override
	public String getID() {
		return muteCommand.getID();
	}
	
	@Override
	public boolean isSupported(Client client) {
		return muteCommand.isSupported(client);
	}
	
}
