package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.Link;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkUtil;
import reactor.core.publisher.Mono;

public class LinkAcceptCommand extends CommandBase {
	
	public LinkAcceptCommand() {
		super("command.link.accept", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	private static LinkAcceptCommand instance;

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//Get the link
		//Check that we have permissions
		//Parse the channel ID
		//Get the link channel, and check that the link IDs match
		//Accept the channel
		Locale locale = data.getLocale();
		
		return parseArguments(parser, locale)
			.flatMap(acceptedChannelID -> runCommand(data.getChannel(), data.getInvoker(), acceptedChannelID, locale));
	}

	private Mono<ChannelID> parseArguments(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		
		ChannelID channelID = parser.eatUncheckedChannelID();
		int clientID = channelID.getConnectionID();
		//TODO
		if (!parser.isEmpty()) {
			String n = parser.eat();
			if (n.equals("d")) {
				clientID = 0;
			} else if (n.equals("n")) {
				clientID = 1;
			}
		}
		
		return Mono.just(new ChannelID(channelID.getConnection(), channelID.getLong(), clientID));
	}
	
	private Mono<Void> runCommand(Channel acceptingChannel, User invoker, ChannelID acceptedChannelID, Locale locale) {
		return acceptingChannel
			.as(LinkChannel.type)
			.flatMap(clchannel -> clchannel.getInfo())
			.flatMap(info -> info.getLink())
			.flatMap(link -> {
				return LinkUtil.checkPerms(acceptingChannel, invoker, locale)
					.then(acceptedChannelID.getChannel())
					.flatMap(channel -> channel.as(LinkChannel.type))
					.filterWhen(channel -> channel.getInfo()
						.map(info -> info.getLinkID() == link.getID()))
					.switchIfEmpty(Mono.error(new TextException(locale.localize("command.link.notjoining"))))
					.flatMap(channel -> verify(channel, acceptingChannel, locale)
						.then(sendSystemAcceptMessage(link, channel, locale)));
			})
			.then();
	}
	
	private Mono<Message> verify(LinkChannel clchannel, Channel invokingChannel, Locale locale) {
		ChannelTextInterface textGrip = invokingChannel.getInterface(ChannelTextInterface.class);
		return clchannel.edit(spec->spec.setVerified(true))
			.then(clchannel.getServer())
			.flatMap(server->{
				return textGrip.send(locale.localize("command.link.verified",
					"id", clchannel.getID().toString(),
					"server", server.getName(),
					"name", clchannel.getName()));
			});
	}
	
	private Mono<Void> sendSystemAcceptMessage(Link link, LinkChannel clchannel, Locale locale) {
		return clchannel.getServer()
			.flatMap(server->{
				return link.sendSystemMessage(locale.localize("command.link.systemverified",
					"id", clchannel.getID().toString(),
					"server", server.getName(),
					"name", clchannel.getName()));
			});
	}

	static {
		instance = new LinkAcceptCommand();
	}
	
	public static Command get() {
		return instance;
	}
	
}
