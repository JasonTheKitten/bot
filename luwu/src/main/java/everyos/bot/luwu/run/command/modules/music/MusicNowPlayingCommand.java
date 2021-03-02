package everyos.bot.luwu.run.command.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.util.TimeUtil;
import reactor.core.publisher.Mono;

public class MusicNowPlayingCommand extends GenericMusicCommand {
	public MusicNowPlayingCommand() {
		super("command.music.nowplaying", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), manager, locale);
	}

	private Mono<Void> runCommand(Channel channel, MusicManager manager, Locale locale) {
		return showPlaying(channel, manager, null, locale);
	}

	public static Mono<Void> showPlaying(Channel channel, MusicManager manager, MusicTrack trackOverride, Locale locale) {
		return Mono.defer(()->{
			MusicTrack currentTrack = trackOverride!=null?
				trackOverride:
				manager.getPlaying();
			AudioTrack np = trackOverride!=null?
				trackOverride.getAudioPart():
				manager.getPlayingAudio();
			AudioTrackInfo info = np.getInfo();
			
			return channel.getInterface(ChannelTextInterface.class).send(spec->{
				if (currentTrack == null) {
					spec.setContent(locale.localize("command.music.nowplaying.nothingplaying"));
					return;
				}
				
				spec.setEmbed(embedSpec->{
					embedSpec.setAuthor(info.author, info.uri, null);
					embedSpec.setTitle(info.title);
					embedSpec.setColor(ChatColor.of(255, 255, 126));
					
					if (trackOverride!=null) {
						embedSpec.setDescription(locale.localize("command.music.nowplaying.queued",
							"len", TimeUtil.formatTime(info.length)));
					} else {
						embedSpec.setDescription(locale.localize("command.music.nowplaying.nowplaying"));
						
						embedSpec.addField(locale.localize("command.music.nowplaying.length"),
							TimeUtil.formatTime(np.getPosition())+"/"+ TimeUtil.formatTime(info.length)+
								" ("+(Math.floor((((double) np.getPosition())/(double) info.length)*1000.)/10.)+"%)", false);
					}
					
					if (currentTrack.getQueuedBy().isPresent()) {
						embedSpec.setFooter(locale.localize("command.music.nowplaying.queuedby",
							"uid", currentTrack.getQueuedBy().get().toString()));
					}
				});
			}).then();
		}).then();
	}

	@Override
	protected boolean requiresDJ() {
		return false;
	}
}
