package everyos.bot.luwu.run.command.modules.role.role;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class RoleListCommand extends CommandBase{

	public RoleListCommand() {
		super("command.role.role.list", e->true,
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
			.flatMap(server->server.as(RoleServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(info->{
				return channel.getInterface(ChannelTextInterface.class).send(spec->{
					spec.setEmbed(embedSpec->{
						embedSpec.setTitle(locale.localize("command.role.role.list.title"));
						embedSpec.setColor(ChatColor.of(126, 255, 126));
						
						Map<String, RoleID> roles = info.getAvailableRoles();
						if (roles.isEmpty()) {
							embedSpec.setDescription(locale.localize("command.role.role.list.noroles"));
						} else {
							StringBuilder builder = new StringBuilder();
							roles.forEach((name, roleID)->{
								builder.append("**"+name+"** - <@&"+roleID.toString()+">\n");
							});
							embedSpec.setDescription(builder.toString());
						}
					});
				});
			})
			.then();
	}

}
