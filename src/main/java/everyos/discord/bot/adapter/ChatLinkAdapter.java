package everyos.discord.bot.adapter;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBArray;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChatLinkAdapter implements IAdapter {
    private ShardInstance shard;
	private long chatlinkID;
	
	public long lastUserID = -1L;
	public long lastTime = -1;
	public long lastChannel = -1L;
	
	public ChatLinkAdapter(ShardInstance shard, Long s) {
        this.shard = shard;
		this.chatlinkID = s;
	}
	
	public Flux<?> onEachChannel(long l, BiFunction<MessageChannel, Long, Mono<?>> mcs) {
		ArrayList<Mono<?>> monos = new ArrayList<Mono<?>>();
		getData((obj, doc)->{
            DBArray arr = obj.getOrCreateArray("links", ()->new DBArray());
            arr.forEach(i->{
            	if (arr.getLong(i)==l) return;
                Mono<?> mono =
                	shard.client.getChannelById(Snowflake.of(arr.getString(i)))
                	.cast(MessageChannel.class)
                	.flatMap(channel->mcs.apply(channel, arr.getLong(i)))
                	.onErrorResume(e->{ return Mono.empty();});
                
                monos.add(mono);
            });
        });
		return Flux.just(monos.toArray()).flatMap(m->(Mono<?>) m);
	}
	
	@SuppressWarnings("unchecked")
	public Flux<Message> forward(long l, BiConsumer<MessageCreateSpec, Long> mcs) {
		ArrayList<Mono<Message>> monos = new ArrayList<Mono<Message>>();
		getData((obj, doc)->{
            DBArray arr = obj.getOrCreateArray("links", ()->new DBArray());
            arr.forEach(i->{
            	if (arr.getLong(i)==l) return;
                Mono<Message> mono = shard.client.getChannelById(Snowflake.of(arr.getString(i)))
                	.cast(MessageChannel.class)
                	.flatMap(channel->channel.createMessage(msg->mcs.accept(msg, arr.getLong(i))))
                	.onErrorResume(e->{ return Mono.empty();});
                
                monos.add(mono);
            });
        });
		return Flux.just(monos.toArray()).flatMap(m->(Mono<Message>) m);
	}
	
	@Override public DBDocument getDocument() {
		return shard.db.collection("chatlinks").getOrSet(chatlinkID, doc->{});
    }
	
	public static ChatLinkAdapter of(ShardInstance shard, Long s) {
		DBDocument linkdb = shard.db.collection("chatlinks").getOrSet(s, doc->{});
		return (ChatLinkAdapter) linkdb.getMemoryOrSet("adapter", ()->new ChatLinkAdapter(shard, s));
	}
	
	public <T> T getData(Function<DBObject, T> func) {
		return getDocument().getObject(func);
	}
    public <T> T getData(BiFunction<DBObject, DBDocument, T> func) {
		return getDocument().getObject(func);
	}
    public void getData(Consumer<DBObject> func) {
		getDocument().getObject(func);
	}
    public void getData(BiConsumer<DBObject, DBDocument> func) {
		getDocument().getObject(func);
	}
}