package everyos.bot.luwu.run.command.modules.music.playlist;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.music.playlist.internal.PlaylistInfo;
import everyos.bot.luwu.run.command.modules.music.playlist.internal.PlaylistUser;
import reactor.core.publisher.Mono;

public class MusicPlaylistListCommand extends CommandBase {
	
	public MusicPlaylistListCommand() {
		super("command.music.playlist.list", e -> true,
			ChatPermission.SEND_MESSAGES | ChatPermission.SEND_EMBEDS,
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
		if (parser.isEmpty()) {
			return Mono.just("");
		}
		if (!parser.couldBeQuote()) {
			return expect(locale, parser, "command.error.quote");
		}
		
		return Mono.just(parser.eatQuote());
	}

	private Mono<Void> runCommand(Channel channel, Member member, String name, Locale locale) {
		return member.as(PlaylistUser.type)
			.flatMap(m -> m.getInfo())
			.flatMap(info->{
				if (name.isEmpty()) {
					StringBuilder desc = new StringBuilder();
					for (PlaylistInfo playlist: info.getPlaylists()) {
						desc.append("**" + playlist.getName() + "**\n");
					}
					return channel.getInterface(ChannelTextInterface.class).send(spec->{
						spec.setEmbed(embed->{
							embed.setTitle(locale.localize("command.music.playlist.list.alllists"));
							embed.setDescription(desc.toString());
						});
					});
				}
				
				return channel.getInterface(ChannelTextInterface.class).send(embed->{
					
				});
			})
			.then();
	}
	
}
