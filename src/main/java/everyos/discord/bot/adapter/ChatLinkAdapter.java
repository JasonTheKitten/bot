package everyos.discord.bot.adapter;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.MessageCreateSpec;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBArray;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChatLinkAdapter implements IAdapter {
    ShardInstance shard;
	String chatlinkID;
	public ChatLinkAdapter(ShardInstance shard, String chatlinkID) {
        this.shard = shard;
		this.chatlinkID = chatlinkID;
	}
	
	public Flux<?> forward(String cid, String text) { return forward(cid, msg->msg.setContent(text)); }
	public Flux<?> forward(String cid, Consumer<MessageCreateSpec> mcs) {
		ArrayList<Mono<?>> monos = new ArrayList<Mono<?>>();
		getData((obj, doc)->{
            DBArray arr = obj.getOrCreateArray("links", ()->new DBArray());
            arr.forEach(i->{
            	if (arr.getString(i).equals(cid)) return;
                Mono<?> mono = shard.client.getChannelById(Snowflake.of(arr.getString(i)))
                	.flatMap(c->((MessageChannel) c).createMessage(mcs))
                	.onErrorResume(e->{ return Mono.empty();});
                
                monos.add(mono);
            });
        });
		return Flux.just(monos.toArray()).flatMap(m->(Mono<?>) m);
	}

	@Override public DBDocument getDocument() {
		return shard.db.collection("chatlinks").getOrSet(chatlinkID, doc->{});
    }
	
	public static ChatLinkAdapter of(ShardInstance shard, String linkid) {
		DBDocument linkdb = shard.db.collection("chatlinks").getOrSet(linkid, doc->{});
		return (ChatLinkAdapter) linkdb.getMemoryOrSet("adapter", ()->new ChatLinkAdapter(shard, linkid));
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