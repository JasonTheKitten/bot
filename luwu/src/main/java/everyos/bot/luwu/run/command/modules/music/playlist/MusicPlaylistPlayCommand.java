package everyos.bot.luwu.run.command.modules.music.playlist;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.modules.music.GenericMusicCommand;
import everyos.bot.luwu.run.command.modules.music.MusicManager;
import reactor.core.publisher.Mono;

public class MusicPlaylistPlayCommand extends GenericMusicCommand {
	public MusicPlaylistPlayCommand() {
		super("command.music.playlist.play", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		return runCommand(data.getChannel());
	}

	private Mono<Void> runCommand(Channel channel) {
		return channel
			.getInterface(ChannelTextInterface.class)
			.send("This command is temporarily disabled. You can privately clone and host V3 of this bot if you currently need it.")
			.then();
	}

	@Override
	protected boolean requiresDJ() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
