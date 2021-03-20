package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardListCommand extends CommandBase {
	public StarboardListCommand() {
		super("command.starboard.list", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.SEND_EMBEDS,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();

		return runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(schannel->schannel.getInfo())
			.flatMap(info->{
				
				if (!info.enabled()) {
					return Mono.error(new TextException(locale.localize("command.starboard.list.unconfigured")));
				}
				
				return channel.getInterface(ChannelTextInterface.class).send(spec->{
					StringBuilder stars = new StringBuilder();
					for (Tuple<Integer, EmojiID> star: info.getEmojiLevels()) {
						stars.append(star.getT1());
						stars.append(" "+star.getT2().getFormatted()+"\n");
					}
					
					spec.setEmbed(embed->{
						embed.setTitle(locale.localize("command.starboard.list.title"));
						embed.setColor(new ChatColor(0, 192, 128));
						embed.addField(locale.localize("command.starboard.list.icon"), info.getStarEmoji().getFormatted(), true);
						embed.addField(locale.localize("command.starboard.list.channel"), "<#"+info.getStarboardChannelID().toString()+">", true);
						embed.addField(locale.localize("command.starboard.list.stars"), stars.toString().strip(), false);
					});
				});
			})
			.then();
	}
}
