package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicShuffleCommand extends GenericMusicCommand {
	public MusicShuffleCommand() {
		super("command.music.shuffle", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), manager, locale);
	}

	private Mono<Void> runCommand(Channel channel, MusicManager manager, Locale locale) {
		return Mono.defer(()->{
			MusicQueue queue = manager.getQueue();
			
			for (int i=0; i<queue.size(); i++) {
				queue.put((int)(Math.random()*(queue.size()-1)), queue.remove(i));
			}
			
			return channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.music.shuffle.message"))
				.then();
		});
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
