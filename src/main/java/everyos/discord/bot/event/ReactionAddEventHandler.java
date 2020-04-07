package everyos.discord.bot.event;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
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

	public Mono<?> handle(ReactionAddEvent e) {
		return e.getMessage().flatMap(message->{	
			String emoji =
				e.getEmoji().asUnicodeEmoji().map(ue->ue.getRaw())
				.orElseGet(()->e.getEmoji().asCustomEmoji().map(ce->ce.getId().asString()).get());
			
			//Reaction role code
			AtomicReference<String> roleID = new AtomicReference<String>();
			MessageAdapter.of(shard, message.getChannelId().asString(), message.getId().asString()).getDocument().getObject(obj->{
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
				AtomicReference<String> sbCID = new AtomicReference<String>();
				GuildAdapter.of(shard, gid.asString()).getDocument().getObject(obj->{
					starID.set(obj.getOrDefaultString("star", null));
					sbCID.set(obj.getOrDefaultString("starc", null));
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
							AtomicReference<String> orgID = new AtomicReference<String>();
							AtomicReference<String> orgCID = new AtomicReference<String>();
							AtomicReference<String> sbID = new AtomicReference<String>();
							AtomicReference<Mono<?>> mono2 = new AtomicReference<Mono<?>>();
							mono2.set(Mono.empty());
							
							String mid = e.getMessageId().asString();
							String mcid = e.getChannelId().asString();
							MessageAdapter.of(shard, mcid, mid).getDocument().getObject((obj, doc)->{
								if (!obj.getOrDefaultBoolean("issb", false)) { //Post is not from starboard
									orgID.set(mid); orgCID.set(mcid);
									if (obj.getOrDefaultString("sbmid", null)!=null) { //Post is already on starboard
										sbID.set(obj.getOrDefaultString("sbmid", null));
									} else { //Post is not yet on starboard
										if (n<1) return; //TODO: Var
										mono2.set(e.getGuild().flatMap(g->g.getChannelById(Snowflake.of(sbCID.get())).cast(MessageChannel.class)
											.flatMap(c->{
												return c.createMessage(msg->{
													msg.setEmbed(embed->{
														embed.setColor(Color.YELLOW);
														embed.setDescription(message.getContent().orElse(""));
														Set<Attachment> s = message.getAttachments();
														if (!s.isEmpty()) embed.setImage(s.iterator().next().getUrl());
														message.getAuthor().ifPresent(a->{
															String url = String.format(
																"https://discordapp.com/channels/%s/%s/%s", 
																gid.asString(),
																e.getChannelId().asString(),
																e.getMessageId().asString());
															embed.setAuthor(a.getUsername()+"#"+a.getDiscriminator(), url, a.getAvatarUrl());
															embed.setFooter("Posted by User ID: "+a.getId().asString(), null);
														}); //TODO: Localize
													});
												});
											}))
											.flatMap(m->{
												sbID.set(m.getId().asString());
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
									orgID.set(obj.getOrDefaultString("ogm", null));
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
