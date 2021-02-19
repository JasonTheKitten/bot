package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicPlayCommand extends GenericMusicCommand {
	public MusicPlayCommand() {
		super("command.music.play", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS|ChatPermission.VC_CONNECT|ChatPermission.VC_SPEAK,
			ChatPermission.NONE);
	}

	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return MusicUtil.lookup(parser.getRemaining()).flatMap(track->{
			manager.getQueue().queue(new MusicTrack(track));
			manager.ready();
			return data.getChannel().getInterface(ChannelTextInterface.class).send(spec->{
				spec.setEmbed(embed->{
					embed.setDescription(locale.localize("command.music.queued",
						"name", track.getInfo().title.replace("\\", "\\\\").replace("`", "\\`"),
						"uid", String.valueOf(data.getInvoker()),
						"uname", data.getInvoker().getHumanReadableID().replace("\\", "\\\\").replace("`", "\\`")));
					embed.setColor(ChatColor.of(0, 0, 0));
				});
			});
		}).then();
	}

	@Override boolean requiresDJ() {
		return true;
	}
}
