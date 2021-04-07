package everyos.bot.luwu.run.command.modules.music.playlist;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.music.playlist.internal.PlaylistUser;
import reactor.core.publisher.Mono;

public class MusicPlaylistDeleteCommand extends CommandBase {
	public MusicPlaylistDeleteCommand() {
		super("command.music.playlist.delete", e->true,
			ChatPermission.SEND_MESSAGES,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(name->runCommand(data.getChannel(), data.getInvoker(), name, locale));
	}

	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeQuote()) {
			return expect(locale, parser, "command.error.quote");
		}
		
		return Mono.just(parser.eatQuote());
	}

	private Mono<Void> runCommand(Channel channel, Member member, String name, Locale locale) {
		return member.as(PlaylistUser.type)
			.flatMap(m->m.edit(spec->{
				spec.deletePlaylist(name);
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.music.playlist.delete.message")))
			.then();
	}
}
