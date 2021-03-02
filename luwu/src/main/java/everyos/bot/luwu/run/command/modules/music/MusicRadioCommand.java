package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicRadioCommand extends GenericMusicCommand {
	public MusicRadioCommand() {
		super("command.music.radio", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.VC_CONNECT|ChatPermission.VC_SPEAK,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.setRadio(!manager.getRadio());
		manager.ready();
		ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
		return channel.send(data.getLocale().localize(
			"command.music.radio."+
			(manager.getRadio()?"on":"off")))
			.then();
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
