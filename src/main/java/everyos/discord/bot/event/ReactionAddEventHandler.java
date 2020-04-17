package everyos.discord.bot.event;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.ShardInstance;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.storage.database.DBObject;
import reactor.core.publisher.Mono;

public class ReactionAddEventHandler {
	private ShardInstance shard;

	public ReactionAddEventHandler(ShardInstance shard) {
		this.shard = shard;
	}

	public Mono<Void> handle(ReactionAddEvent e) {
		return e.getMessage().flatMap(message->{	
			String emoji =
				e.getEmoji().asUnicodeEmoji().map(ue->ue.getRaw())
				.orElseGet(()->e.getEmoji().asCustomEmoji().map(ce->ce.getId().asString()).get());
			
			//Reaction role code
			AtomicReference<String> roleID = new AtomicReference<String>();
			MessageAdapter.of(shard, message.getChannelId().asLong(), message.getId().asLong()).getDocument().getObject(obj->{
				roleID.set(obj.getOrDefaultObject("roles", new DBObject()).getOrDefaultString(emoji, null));
			});
			
			Mono<Void> mono = Mono.empty();
			if (roleID.get()!=null) {
				mono=mono.and(e.getMember().get().addRole(Snowflake.of(roleID.get())));
			}
			
			//Starboard code
			Snowflake gid = e.getGuildId().orElse(null);
			if (gid!=null) {
				AtomicReference<String> starID = new AtomicReference<String>();
				AtomicLong sbCID = new AtomicLong();
				GuildAdapter.of(shard, gid.asLong()).getDocument().getObject(obj->{
					starID.set(obj.getOrDefaultString("star", null));
					sbCID.set(obj.getOrDefaultLong("starc", -1));
				});
				
				if (emoji.equals(starID.get())) {
					ArrayList<Long> reactors = new ArrayList<Long>();
					mono=mono.and(message.getReactors(e.getEmoji())
						.doOnNext(m->{
							long mid = m.getId().asLong();
							if (!reactors.contains(mid)) reactors.add(mid);
						})
						.count()
						.flatMap(n->{
							AtomicLong orgID = new AtomicLong();
							AtomicLong orgCID = new AtomicLong();
							AtomicLong sbID = new AtomicLong();
							AtomicReference<Mono<?>> mono2 = new AtomicReference<Mono<?>>();
							mono2.set(Mono.empty());
							
							long mid = e.getMessageId().asLong();
							long mcid = e.getChannelId().asLong();
							MessageAdapter.of(shard, mcid, mid).getDocument().getObject((obj, doc)->{
								if (!obj.getOrDefaultBoolean("issb", false)) { //Post is not from starboard
									orgID.set(mid); orgCID.set(mcid);
									if (obj.getOrDefaultString("sbmid", null)!=null) { //Post is already on starboard
										sbID.set(obj.getOrDefaultLong("sbmid", -1));
									} else { //Post is not yet on starboard
										if (n<1) return; //TODO: Var
										mono2.set(e.getGuild().flatMap(g->g.getChannelById(Snowflake.of(sbCID.get())).cast(MessageChannel.class)
											.flatMap(c->{
												return c.createMessage(msg->{
													msg.setEmbed(embed->{
														embed.setColor(Color.YELLOW);
														embed.setDescription(message.getContent());
														Set<Attachment> s = message.getAttachments();
														if (!s.isEmpty()) embed.setImage(s.iterator().next().getUrl());
														message.getAuthor().ifPresent(a->{
															String url = String.format(
																"https://discordapp.com/channels/%s/%s/%s", 
																gid.asLong(),
																e.getChannelId().asLong(),
																e.getMessageId().asLong());
															embed.setAuthor(a.getUsername()+"#"+a.getDiscriminator(), url, a.getAvatarUrl());
															embed.setFooter("Posted by User ID: "+a.getId().asLong(), null);
														}); //TODO: Localize
													});
												});
											}))
											.flatMap(m->{
												sbID.set(m.getId().asLong());
												obj.set("sbmid", sbID.get());
												MessageAdapter.of(shard, sbCID.get(), sbID.get()).getDocument().getObject((obj2, doc2)->{
													obj2.set("issb", true);
													doc2.save();
												});
												return Mono.empty();
											}));
									}
								} else { //Post is from starboard
									sbID.set(mid); sbCID.set(mcid);
									orgID.set(obj.getOrDefaultLong("ogm", -1));
								}
								doc.save();
							});
							return mono2.get(); //TODO: More
						}));
				}
			}
			
			return mono.onErrorResume(ex->{ex.printStackTrace(); return Mono.empty();});
		})
		.onErrorResume(err->{err.printStackTrace(); return Mono.empty();});
	}
}
