package everyos.bot.luwu.run.command.modules.starboard;

import java.util.ArrayList;
import java.util.List;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.event.ReactionEvent;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.core.functionality.message.MessageReactionInterface;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StarboardHooks {
	public static Mono<Void> starboardHook(ReactionEvent event) {
		Locale locale = null;
		
		return event.getMessage()
			.flatMap(message->message.as(StarboardMessage.type))
			.flatMap(message->{
				return message.getInfo()
					.flatMap(info->info.getStarboardServer()
						.flatMap(server->server.getInfo())
						.zipWith(Mono.just(info)))
					.filter(d->d.getT1().enabled()&&d.getT1().getStarEmoji().equals(event.getReaction()))
					.flatMap(d->(d.getT2().isStarboardMessage()?Mono.just(message):processOriginalMessage(d.getT1(), message, d.getT2(), locale))
						.flatMap(m->updateStarboardPost(m, d.getT1(), d.getT2())));
			})
			.then();
	}
	
	// Get message data
	// If message original
	//   If is on starboard, return starboard
	//   If not on starboard, check eligibility
	//     Create starboard post
	//     Return starboard
	// If message starboard
	//   Return starboard
	// Get starboard and message
	//   Calculate reactions
	//   Edit message content
	
	//TODO: Prevent self stars
	//TODO: Localize

	private static Mono<StarboardMessage> processOriginalMessage(StarboardInfo sinfo, StarboardMessage message,
		StarboardMessageInfo info, Locale locale) {
		
		if (info.hasStarboardMessage()) {
			return info.getStarboardMessage();
		}
		return checkEligibility(message, info)
			.filter(v->v)
			.flatMap(v->createStarboardMessage(sinfo, message, locale));
	}
	
	private static Mono<Boolean> checkEligibility(StarboardMessage message, StarboardMessageInfo info) {
		return info.getStarboardServer()
			.flatMap(server->server.getInfo())
			.flatMap(sinfo->{
				int required = sinfo.getEmojiLevels()[0].getT1();
				
				return countReactions(sinfo.getStarEmoji(), message)
					.map(count->count>=required);
			});
	}

	private static Mono<Integer> countReactions(EmojiID emoji, StarboardMessage... message) {
		return Flux.fromArray(message)
			.flatMap(m->m.getInterface(MessageReactionInterface.class).getReactors(emoji))
			.collectList()
			.map(list->{
				List<User> users = new ArrayList<>();
				for (User[] usersRaw: list) {
					for (User user: usersRaw) {
						if (!user.isBot()&&!users.contains(user)) {
							users.add(user);
						}
					}
				}
				
				return users.size();
			});
	}

	private static Mono<StarboardMessage> createStarboardMessage(StarboardInfo sinfo, StarboardMessage message, Locale locale) {
		return message.getChannel()
			.flatMap(c->c.getServer())
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(s->{
				return message.getAuthor().flatMap(author->{ //TODO: Allow staring bot messages
					return s.getInterface(ChannelTextInterface.class).send(spec->{
						spec.setContent("Still processing. Hold on a second..."); //TODO: Localize
						spec.setEmbed(embed->{
							embed.setColor(ChatColor.of(255, 128, 0));
							embed.setDescription(message.getContent().orElse(""));
							/*Set<Attachment> s = message.getAttachments();
							if (!s.isEmpty()) embed.setImage(s.iterator().next().getUrl());*/
							//TODO
							String url = String.format(
								"https://discordapp.com/channels/%s/%s/%s", 
								s.getID().getLong(), message.getChannelID(), message.getMessageID().getLong());
							embed.setAuthor(author.getHumanReadableID(), url, author.getAvatarUrl().orElse(null));
							embed.setFooter("Posted by User ID: "+author.getID().getLong());
							//TODO: Set custom time
						});
					})
					.flatMap(m->m.getInterface(MessageReactionInterface.class).addReaction(sinfo.getStarEmoji())
						.then(Mono.just(m)));
				})
				.flatMap(m->m.as(StarboardMessage.type))
				.flatMap(m->{
					return message.editInfo(spec->{
						spec.setStarboardMessage(m);
					}).then(m.editInfo(spec->{
						spec.setOriginalMessage(message);
					})).then(Mono.just(m));
				});
			});
	}

	private static Mono<Void> updateStarboardPost(StarboardMessage message, StarboardInfo sinfo, StarboardMessageInfo info) {
		return message.getInfo()
			.flatMap(inf->inf.getOriginalMessage())
			.flatMap(o->countReactions(sinfo.getStarEmoji(), message, o))
			.flatMap(reactions->{
				EmojiID icon = null;
				Tuple<Integer, EmojiID>[] icons = sinfo.getEmojiLevels();
				for (int i=0; i<icons.length; i++) {
					if ((icons[i].getT1()-1)<=reactions) {
						icon = icons[i].getT2();
					}
				}	
				String iconPart = icon==null?"\uD83D\uDEAB ":icon.getFormatted()+" ";
				
				return message.edit(spec->{
					spec.setContent(iconPart+String.valueOf(reactions));
				}).then();
			});
	}
}
