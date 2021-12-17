package everyos.bot.luwu.run.command.modules.tickets.setup;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.tickets.channel.TicketChannel;
import everyos.bot.luwu.run.command.modules.tickets.member.TicketMember;
import everyos.bot.luwu.run.command.modules.tickets.server.TicketServer;
import reactor.core.publisher.Mono;

public class TicketCreateCommand extends CommandBase {
	
	public TicketCreateCommand() {
		super("command.ticket.create", e -> true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_CHANNELS,
			ChatPermission.SEND_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			checkEnabled(data.getChannel(), locale)
			.then(checkUserHasNoTicket(data.getInvoker(), locale))
			.then(runCommand(data.getInvoker(), data.getChannel(), locale));
	}

	private Mono<Void> checkEnabled(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server -> server.as(TicketServer.type))
			.flatMap(server -> server.getInfo())
			.flatMap(server -> {
				if (!server.getEnabled()) {
					return Mono.error(new TextException(locale.localize("command.ticket.create.disabled")));
				}
				return Mono.empty();
			});
	}
	
	private Mono<Void> checkUserHasNoTicket(Member invoker, Locale locale) {
		return invoker
			.as(TicketMember.type)
			.getInfo()
			.filter(info -> info.getTicketChannelID().isPresent())
			.flatMap(info -> info.getTicketChannel())
			.onErrorResume(e -> Mono.empty())
			.flatMap(_1 -> Mono.error(new TextException(locale.localize("command.ticket.create.alreadycreated"))))
			.then();
	}
	
	private Mono<Void> runCommand(Member invoker, Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server -> server.as(TicketServer.type))
			.flatMap(server -> server.createChannel(spec->{
				spec.setName(locale.localize("command.ticket.create.name")+'-'+invoker.getHumanReadableID().substring(0, 15));
				spec.setTopic(locale.localize("command.ticket.create.topic"));
				spec.setReason(locale.localize("command.ticket.create.topic")+" "+invoker.getID().toString());
				
				int botRoles = ChatPermission.SEND_MESSAGES | ChatPermission.SEND_MESSAGES;
				int userRoles =
					ChatPermission.SEND_MESSAGES | ChatPermission.ADD_REACTIONS | ChatPermission.USE_EXTERNAL_EMOJIS |
					ChatPermission.SEND_EMBEDS | ChatPermission.VIEW_CHANNEL;
				//int modRoles = userRoles;
				
				spec.setRoleOverride(RoleID.EVERYONE, ChatPermission.NONE, ChatPermission.ALL);
				spec.setMemberOverride(invoker.getID().getLong(), userRoles, ChatPermission.NONE);
				spec.setMemberOverride(invoker.getConnection().getSelfID(), botRoles, ChatPermission.NONE);
				//TODO: Ticket reviewer role
			})
			.flatMap(ticketChannel -> ticketChannel.as(TicketChannel.type))
			.flatMap(ticketChannel -> {
				String message = locale.localize("command.ticket.create.default");
				Mono<Void> m1 = ticketChannel.getInterface(ChannelTextInterface.class).send(spec -> {
					spec.setPresanitizedContent("<@"+invoker.getID().getLong()+">\n"+message);
				}).then();
				Mono<Void> m2 = channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.ticket.create.message")).then();
				Mono<Void> m3 = ticketChannel.edit(spec -> spec.configure());
				Mono<Void> m4 = invoker
					.as(TicketMember.type)
					.edit(spec -> spec.setTicketChannel(ticketChannel.getID()));
				
				return Mono.when(m1, m2, m3, m4);
			})).then();
	}
}
