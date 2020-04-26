package everyos.discord.bot.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBArray;
import everyos.discord.bot.database.DBDocument;
import everyos.discord.bot.database.DBObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChatLinkAdapter implements IAdapter {
	private static HashMap<Long, ChatLinkAdapter> cache;
	static {
		cache = new HashMap<Long, ChatLinkAdapter>();
	}
	
    private BotInstance bot;
	private long chatlinkID;
	
	public long lastUserID = -1L;
	public long lastTime = -1;
	public long lastChannel = -1L;
	
	public ChatLinkAdapter(BotInstance bot, Long s) {
        this.bot = bot;
		this.chatlinkID = s;
	}
	
	public Flux<?> onEachChannel(long exclude, BiFunction<MessageChannel, Long, Mono<?>> mcs) {
		return getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			ArrayList<Mono<?>> monos = new ArrayList<Mono<?>>();
			
            DBArray arr = obj.getOrCreateArray("links", ()->new DBArray());
            arr.forEach(val->{
            	long cid = (long) val;
            	if (cid==exclude) return;
                Mono<?> mono =
                	bot.client.getChannelById(Snowflake.of(cid))
                	.cast(MessageChannel.class)
                	.flatMap(channel->mcs.apply(channel, cid))
                	.onErrorResume(e->{ return Mono.empty();});
                
                monos.add(mono);
            });
            
            return Mono.just(monos);
        }).flatMapMany(monos->Flux.just(monos.toArray())).flatMap(m->(Mono<?>) m);
	}
	
	@SuppressWarnings("unchecked")
	public Flux<Message> forward(long exclude, BiConsumer<MessageCreateSpec, Long> mcs) {
		return getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			ArrayList<Mono<Message>> monos = new ArrayList<Mono<Message>>();
			
            DBArray arr = obj.getOrCreateArray("links", ()->new DBArray());
            arr.forEach(val->{
            	long cid = (long) val;
            	if (cid==exclude) return;
                Mono<Message> mono = bot.client.getChannelById(Snowflake.of(cid))
                	.cast(MessageChannel.class)
                	.flatMap(channel->channel.createMessage(msg->mcs.accept(msg, cid)))
                	.onErrorResume(e->{ return Mono.empty();});
                
                monos.add(mono);
            });
            
            return Mono.just(monos);
        }).flatMapMany(monos->Flux.just(monos.toArray())).flatMap(m->(Mono<Message>) m);
	}
	
	public static ChatLinkAdapter of(BotInstance bot, Long s) {
		if (!cache.containsKey(s)) cache.put(s, new ChatLinkAdapter(bot, s));
		return cache.get(s);
	}
	
	@Override public Mono<DBDocument> getDocument() {
        return bot.db.collection("chatlinks").scan().with("clid", chatlinkID).orSet(doc->{});
    }
}