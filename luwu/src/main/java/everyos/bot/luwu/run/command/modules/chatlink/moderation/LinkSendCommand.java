package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import java.time.Duration;
import java.util.Optional;

import everyos.bot.chat4j.entity.ChatAttachment;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.core.functionality.message.MessageReactionInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.Link;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkAttachment;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkInfo;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkMessage;
import everyos.bot.luwu.run.command.modules.chatlink.user.LinkUser;
import reactor.core.publisher.Mono;

public class LinkSendCommand extends CommandBase {

	public LinkSendCommand() {
		super("command.link.send", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.SEND_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			createLinkMessageFor(data.getMessage())
			.flatMap(linkMessage -> runCommand(linkMessage, data.getMessage(), locale));
	}
	
	private Mono<Void> runCommand(LinkMessage linkMessage, Message message, Locale locale) {
		MessageReactionInterface reactions = message.getInterface(MessageReactionInterface.class);
		return linkMessage.getOriginChannel().getInfo()
			.flatMap(info -> {
				return Mono.just(info.isVerified())
					.filter(v2->v2)
					.switchIfEmpty(Mono.error(new TextException(locale.localize("command.link.error.needverified"))))
					.then(info.getLink())
					.flatMap(link->
						checkAgreement(link, linkMessage.getSender(), message.getContent().orElse(""), locale)
						.onErrorResume(e->{
							if (e instanceof TextException) {
								return linkMessage
									.getOriginChannel()
									.getInterface(ChannelTextInterface.class)
									.send(e.getMessage())
									.then(Mono.error(e));
							}
							return Mono.error(e);
						})
						.then(link.sendMessage(linkMessage)));
			})
			.then(reactions.addReaction(EmojiID.of("\u2611")))
			.thenReturn(true) // .delayElement requires a non-empty mono
			.delayElement(Duration.ofMillis(1000))
			.then(reactions.removeReaction(EmojiID.of("\u2611")))
			.onErrorResume(e->{
				if (!(e instanceof TextException)) {
					e.printStackTrace();
				}
				return reactions.addReaction(EmojiID.of("\u274C"));
			});
	}
	
	Mono<LinkMessage> createLinkMessageFor(Message message) {
		return message
			.getChannel()
			.flatMap(channel -> channel.as(LinkChannel.type))
			.flatMap(channel -> {
				return channel
					.getInfo()
					.flatMap(info -> message.getAuthor()
						.flatMap(author -> author.as(LinkUser.typeWith(info.getLinkID()))))
					.zipWith(Mono.just(channel));
			})
			.map(tup -> createLinkMessageFor(message, tup.getT2(), tup.getT1()));
		
	}
	
	private LinkMessage createLinkMessageFor(Message message, LinkChannel channel, LinkUser user) {
		return new LinkMessage() {
			@Override
			public LinkUser getSender() {
				return user;
			}

			@Override
			public LinkChannel getOriginChannel() {
				return channel;
			}

			@Override
			public Optional<String> getContent() {
				return message.getContent();
			}

			@Override
			public LinkAttachment[] getAttachments() {
				ChatAttachment[] attachments = message.getAttachments();
				LinkAttachment[] linkAttachments = new LinkAttachment[attachments.length];
				for (int i = 0; i < attachments.length; i++) {
					ChatAttachment attachment = attachments[i];
					linkAttachments[i] = new LinkAttachment() {
						@Override
						public String getURL() {
							return attachment.getURL();
						}

						@Override
						public String getName() {
							return attachment.getName();
						}
						
						@Override
						public boolean isSpoiler() {
							return attachment.isSpoiler();
						}
					};
				}
				
				return linkAttachments;
			}
		};
	}

	private Mono<Void> checkAgreement(Link link, LinkUser invoker, String text, Locale locale) {
		LinkInfo linkInfo = link.getInfo();
		return invoker
			.getInfo()
			.flatMap(userInfo -> {	
				if (text.equalsIgnoreCase("agree") && !userInfo.isVerified()) {
					return invoker
						.edit(spec -> spec.setVerified(true))
						.then(Mono.error(new TextException(locale.localize("command.link.userverified"))));
				}
				
				return Mono.just(userInfo.isVerified())
					.filter(v->v)
					.switchIfEmpty(
						Mono.error(new TextException(locale.localize("command.link.error.agreement",
							"rules", "> " + linkInfo.getRules().orElse(locale.localize("command.link.norules"))
							.replace("\n", "\n> "))))
					)
					.then();
			});
	}

}
