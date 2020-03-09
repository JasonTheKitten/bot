package everyos.discord.bot.standards;

import java.util.function.Consumer;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.storage.database.DBDocument;
import reactor.core.publisher.Mono;

public class ChannelDocumentCreateStandard implements Consumer<DBDocument> {
    public static ChannelDocumentCreateStandard standard;

    static {
        standard = new ChannelDocumentCreateStandard();
    }

    public ChannelDocumentCreateStandard() {}

    @Override public void accept(DBDocument doc) {
        
    }

    public static void ifExists(String channelID, Consumer<ChannelAdapter> func, Runnable orelse) {
    	Main.db.collection("channels").getIfPresent(channelID, gdoc->{
    		func.accept(ChannelAdapter.of(channelID));
    	}).elsedo(()->{
    		Mono<User> mono = Main.client.getUserById(Snowflake.of(channelID));
    		mono.subscribe(user->{
    			func.accept(ChannelAdapter.of(channelID));
    		});
    		mono.doOnError(throwable->{
    			orelse.run();
    		});
        });
    }
}