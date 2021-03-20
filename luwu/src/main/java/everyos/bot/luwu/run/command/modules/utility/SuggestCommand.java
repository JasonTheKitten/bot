package everyos.bot.luwu.run.command.modules.utility;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.core.functionality.message.MessageReactionInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class SuggestCommand extends CommandBase{

	public SuggestCommand() {
		super("command.suggest", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS|ChatPermission.ADD_REACTIONS,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, locale)
			.flatMap(argument->sendSuggestion(data.getChannel(), data.getInvoker(), argument, locale))
			.then();
	}
	
	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return expect(locale, parser, "command.error.string");
		}
		return Mono.just(parser.getRemaining());
	}

	public static Mono<Message> sendSuggestion(Channel channel, User author, String argument, Locale locale) {
		return channel.getInterface(ChannelTextInterface.class).send(spec->{
			spec.setEmbed(embed->{
				embed.setTitle(locale.localize("command.suggest.by", "user", author.getHumanReadableID()));
				embed.setDescription(argument);
				embed.setFooter(locale.localize("command.suggest.footer", "id", author.getID().toString()));
			});
		}).flatMap(message->{
			MessageReactionInterface reactions = message.getInterface(MessageReactionInterface.class);
			return reactions.addReaction(EmojiID.of("\u2705")).and(
				reactions.addReaction(EmojiID.of("\u274C")))
				.then(Mono.just(message));
		});
	}
}
