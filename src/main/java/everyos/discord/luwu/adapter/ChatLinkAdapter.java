package everyos.discord.luwu.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBDocument;
import everyos.discord.luwu.database.DBObject;
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
	
	public Flux<?> onEachChannel(long exclude, long uid, BiFunction<MessageChannel, Long, Mono<?>> mcs) {
		return getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			ArrayList<Mono<?>> monos = new ArrayList<Mono<?>>();
			
			DBArray gignored = obj.getOrCreateArray("muted", ()->new DBArray());
			if (gignored.contains(uid)) return Mono.error(new Exception());
			
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
	public Flux<Message> forward(long exclude, long uid, BiConsumer<MessageCreateSpec, Long> mcs) {
		return getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			ArrayList<Mono<Message>> monos = new ArrayList<Mono<Message>>();
			
			DBArray gignored = obj.getOrCreateArray("muted", ()->new DBArray());
			if (gignored.contains(uid)) return Mono.error(new Exception());
			
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