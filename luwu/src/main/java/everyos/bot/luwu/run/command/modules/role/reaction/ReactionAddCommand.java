package everyos.bot.luwu.run.command.modules.role.reaction;

import java.util.ArrayList;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.MessageID;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.functionality.message.MessageReactionInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactionAddCommand extends CommandBase {

	public ReactionAddCommand() {
		super("command.role.reaction.add", e->true,
			ChatPermission.ADD_REACTIONS|ChatPermission.MANAGE_ROLES,
			ChatPermission.ADD_REACTIONS|ChatPermission.MANAGE_ROLES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			parseArguments(data.getChannel(), parser, data.getLocale())
			.flatMap(args->createReaction(args.getT1(), args.getT2()))
			.then(data.getMessage().delete());
	}

	

	private Mono<Tuple<MessageID, Tuple<EmojiID, RoleID>[]>> parseArguments(Channel channel, ArgumentParser parser, Locale locale) {
		/*
		 reaction add [mid]
		 	[emoji] [roleid]
		 	[emoji] [roleid]
		 */
		
		//TODO: Validate input
		//TODO: Test why (some) errors are not notifying the user
		//TODO: Modifiers: Switch, Permanent
		
		ArrayList<Tuple<EmojiID, RoleID>> reactionRoles = new ArrayList<>();
		
		MessageID messageID = parser.eatMessageID(channel.getID());
		
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
	
	private Mono<Void> createReaction(MessageID messageID, Tuple<EmojiID, RoleID>[] reactionTups) {
		return messageID.getMessage()
			.flatMap(message->message.as(ReactionMessage.type))
			.flatMapMany(message->{
				return message.editInfo(spec->{
					for (Tuple<EmojiID, RoleID> reactionTup: reactionTups) {
						spec.addReaction(reactionTup.getT1(), reactionTup.getT2());
					}
				}).thenMany(Flux.fromArray(reactionTups).flatMap(tup->{
					return message.getInterface(MessageReactionInterface.class).addReaction(tup.getT1());
				}));
			}).then();
	}

}
