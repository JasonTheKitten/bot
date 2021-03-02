package everyos.bot.luwu.run.command.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.chat4j.functionality.message.EmbedSpec;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.util.TimeUtil;
import reactor.core.publisher.Mono;

public class MusicQueueCommand extends GenericMusicCommand {
	public MusicQueueCommand() {
		super("command.music.queue", e->true, ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(page->runCommand(data.getChannel(), manager, page, locale));
	}
	
	private Mono<Integer> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.just(1);
		}
		
		if (!parser.isNumerical()||Integer.valueOf(parser.peek())<=0) {
			return expect(locale, parser, "command.error.positiveinteger");
		}
		
		int page = (int) parser.eatNumerical();
		
		return Mono.just(page);
	}

	private Mono<Void> runCommand(Channel channel, MusicManager manager, int page, Locale locale) {
		if (manager.getQueue().size()==0) {
			return MusicNowPlayingCommand.showPlaying(channel, manager, null, locale);
		}
		return showQueue(channel, manager, page, locale);
	}

	public static Mono<Void> showQueue(Channel channel, MusicManager manager, int initialPage, Locale locale) {
		return channel.getInterface(ChannelTextInterface.class).send(spec->{
			MusicQueue queue = manager.getQueue();
			
			int queuePageLength = 10;
			int page = initialPage>queue.size()/queuePageLength+1?queue.size()/queuePageLength+1:initialPage;
			
			if (page!=initialPage) {
				spec.setContent(locale.localize("command.music.queue.nosuchpage"));
			}
			
			spec.setEmbed(embedSpec->{
				embedSpec.setTitle(locale.localize("command.music.queue.title", "page", String.valueOf(page)));
				embedSpec.setColor(ChatColor.of(255, 192, 126));
				
				int min = (page-1)*queuePageLength;
				int max = min+queuePageLength>queue.size()?queue.size():min+queuePageLength;
				
				long totalTime = 0L;
				
				MusicTrack playing = manager.getPlaying();
				if (playing!=null) {
					totalTime+=addTrackToEmbed(embedSpec, locale.localize("command.music.queue.current"),
						playing, locale);
				}
				for (int i=min; i<max; i++) {
					totalTime+=addTrackToEmbed(embedSpec, locale.localize("command.music.queue.trackno",
						"no", String.valueOf(i+1)), queue.read(i), locale);
				}
				
				embedSpec.setFooter(locale.localize("command.music.queue.totaltime", "length", TimeUtil.formatTime(totalTime)));
			});
		}).then();
	}
	
	private static long addTrackToEmbed(EmbedSpec spec, String name, MusicTrack track, Locale locale) {
		AudioTrackInfo info = track.getAudioPart().getInfo();
		
		StringBuilder listing = new StringBuilder();
		listing.append("**"+locale.localize("command.music.queue.name")+":** "+info.title+"\n");
		listing.append("**"+locale.localize("command.music.queue.length")+":** "+TimeUtil.formatTime(info.length));
		if (track.getQueuedBy().isPresent()) {
			listing.append("\n**"+locale.localize("command.music.queue.queuedby")+":** <@"+track.getQueuedBy().get().toString()+">");
		}
		
		spec.addField(name, listing.toString(), false);
		
		return info.length;
	}

	@Override
	protected boolean requiresDJ() {
		return false;
	}

}
