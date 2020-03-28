package everyos.discord.bot.event;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.GuildMessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.discord.bot.adapter.ChannelUserAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.usercase.DefaultUserCase;
import everyos.discord.bot.usercase.IgnoreUserCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MessageCreateEventHandler {
	private ShardInstance shard = null;
	private HashMap<String, ICommand> channels;
	public MessageCreateEventHandler(ShardInstance shard) {
		this.shard = shard;
		
		channels = new HashMap<String, ICommand>();
		channels.put("default", new DefaultUserCase(shard));
		channels.put("ignore", new IgnoreUserCase(shard)); //NOTE: User cases are per-channel, while ignore is guild-wide. Must handle
	}
	
	public Flux<?> handle(MessageCreateEvent event) {
		return Flux.just(event.getMessage())
            .filter(message->message.getAuthor().isPresent()&&!message.getAuthor().get().isBot())
            .doOnNext(message->{
                message
                    .getChannel()
                    .doOnError(e->e.printStackTrace())
                    .flatMap(channel->GuildAdapter.of(shard, channel))
                    .doOnNext(adapter->{
                        String uid = message.getAuthor().get().getId().asString();
                        MemberAdapter.of(adapter, uid).getDocument().getObject((object, document)->{
                            long time = 1000*15;
                            long curtime = System.currentTimeMillis();
                            if (curtime-object.getOrDefaultLong("lastmsg", -time)>=time) {
                            	object.set("feth", object.getOrDefaultLong("feth", 0)+1);
                            	object.set("xp", object.getOrDefaultLong("xp", 0)+1); //TODO: Level awards
                                object.set("lastmsg", curtime);
                                document.save();
                            }
                        });
                    })
                    .onErrorResume(e->{e.printStackTrace(); return Mono.empty();})
                    .subscribe();
            })
            .map(message->{
                return message; //TODO: Filter
            })
            .flatMap(message->{
            	return message.getChannel().flatMap(channel->{
	                AtomicReference<String> cmode = new AtomicReference<String>();
	                ChannelUserAdapter.of(shard, message.getChannelId().asString(), message.getAuthor().get()).getDocument().getObject(obj->{
	                    cmode.set(obj.getOrDefaultString("case", "default"));
	                });
	                if (channel instanceof GuildMessageChannel) {
	                	String gid = ((GuildMessageChannel) channel).getGuildId().asString();
	                	String uid = message.getAuthor().get().getId().asString();
	                	MemberAdapter.of(GuildAdapter.of(shard, gid), uid).getDocument().getObject(obj->{
	                		if (obj.getOrDefaultBoolean("ignored", false)) cmode.set("ignore");
	                	});
	                }
	                
	                LocalizationProvider provider = new LocalizationProvider(Localization.en_US);
	                CommandData data = new CommandData(provider, shard);
	
	                return
	                    channels.getOrDefault(cmode.get(), channels.get("default"))
	                        .execute(message, data, message.getContent().orElse(""))
	                    .onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
            	});
            })
            .onErrorResume(e->{e.printStackTrace(); return Flux.empty();});
	}
}
