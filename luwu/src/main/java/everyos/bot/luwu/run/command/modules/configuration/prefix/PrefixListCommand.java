package everyos.bot.luwu.run.command.modules.configuration.prefix;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class PrefixListCommand extends CommandBase {
	
	public PrefixListCommand() {
		super("command.prefix.list", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.SEND_EMBEDS,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return 
			runCommand(data.getChannel(), locale);
	}
	
	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(PrefixServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(info->channel.getInterface(ChannelTextInterface.class).send(spec->{
				spec.setEmbed(embed->{
					embed.setColor(new ChatColor(0, 192, 128));
					embed.setTitle(locale.localize("command.prefix.list.title"));
					StringBuilder desc = new StringBuilder();
					for (String prefix: info.getPrefixes()) {
						desc.append("`"+prefix+"`, ");
					}
					String fin = desc.toString();
					embed.setDescription(fin.substring(0, fin.length()-2));
				});
			}))
			.then();
	}
}
