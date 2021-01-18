package everyos.bot.luwu.run.command.modules.role.reaction;

import java.util.ArrayList;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.MessageID;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactionAddCommand extends CommandBase {

	public ReactionAddCommand() {
		super("command.role.reaction.add");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			parseArguments(parser, data.getLocale())
			.flatMap(args->createReaction(args));
	}

	

	private Mono<Tuple<MessageID, Tuple<EmojiID, RoleID>[]>> parseArguments(ArgumentParser parser, Locale locale) {
		/*
		 reaction add [mid]
		 	[emoji] [roleid]
		 	[emoji] [roleid]
		 */
		
		//TODO: Validate input
		
		ArrayList<Tuple<EmojiID, RoleID>> reactionRoles = new ArrayList<>();
		
		MessageID messageID = parser.eatMessageID();
		
		while (!parser.isEmpty()) {
			EmojiID reactionID = parser.eatEmojiID();
			RoleID roleID = parser.eatRoleID();
			
			reactionRoles.add(Tuple.of(reactionID, roleID));
		}
		
		@SuppressWarnings("unchecked")
		Tuple<EmojiID, RoleID>[] reactionRolesArray = new Tuple[reactionRoles.size()];
		reactionRoles.toArray(reactionRolesArray);
		
		return Mono.just(Tuple.of(messageID, reactionRolesArray));
	}
	
	private Mono<Void> createReaction(Tuple<MessageID, Tuple<EmojiID, RoleID>[]> args) {
		MessageID messageID = args.getT1();
		return messageID.getMessage().flatMapMany(message->{
			return Flux.fromArray(args.getT2()).flatMap(tup->{
				return message.addReaction(tup.getT1());
			});
		}).then();
	}

}
