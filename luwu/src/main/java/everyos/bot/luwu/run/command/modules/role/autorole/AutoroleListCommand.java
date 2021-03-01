package everyos.bot.luwu.run.command.modules.role.autorole;

import java.util.Optional;

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

public class AutoroleListCommand extends CommandBase {

	public AutoroleListCommand() {
		super("command.role.autorole.list", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.SEND_EMBEDS,
			ChatPermission.NONE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(AutoroleServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(info->{
				return channel.getInterface(ChannelTextInterface.class).send(spec->{
					spec.setEmbed(embedSpec->{
						embedSpec.setTitle(locale.localize("command.role.autorole.list.title"));
						embedSpec.setColor(ChatColor.of(255, 126, 126));
						
						RoleID[] roles = info.getDefaultRoles();
						Optional<RoleID> botRole = info.getBotRole();
						Optional<RoleID> userRole = info.getUserRole();
						if (roles.length==0&&botRole.isEmpty()&&userRole.isEmpty()) {
							embedSpec.setDescription(locale.localize("command.role.autorole.list.noroles"));
						} else {
							if (roles.length!=0) {
								StringBuilder builder = new StringBuilder();
								for (RoleID roleID: roles) {
									builder.append("<@&"+roleID.toString()+">\n");
								};
								embedSpec.addField(locale.localize("command.role.autorole.list.default"), builder.toString(), false);
							}
							if (botRole.isPresent()) {
								embedSpec.addField(locale.localize("command.role.autorole.list.botrole"),
									"<@&"+botRole.get().toString()+">\n", false);
							}
							if (userRole.isPresent()) {
								embedSpec.addField(locale.localize("command.role.autorole.list.userrole"),
									"<@&"+userRole.get().toString()+">\n", false);
							}
						}
					});
				});
			})
			.then();
	}

}
