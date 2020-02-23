package everyos.discord.bot.standards;

import java.util.function.Consumer;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.adapter.IAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.storage.database.DBDocument;
import reactor.core.publisher.Mono;

public class MemberDocumentCreateStandard implements Consumer<DBDocument> {
    public static Consumer<DBDocument> standard;

    static {
        standard = new MemberDocumentCreateStandard();
    }

    @Override public void accept(DBDocument doc) {}
    
    public static void ifExists(IAdapter padapter, String memberID, Consumer<MemberAdapter> func, Runnable orelse) {
    	padapter.getDocument().subcollection("members").getIfPresent(memberID, gdoc->{
    		func.accept(MemberAdapter.of(padapter, memberID));
    	}).elsedo(()->{
    		System.out.println(memberID);
    		Mono<User> mono = Main.client.getUserById(Snowflake.of(memberID));
    		mono.subscribe(user->{
    			func.accept(MemberAdapter.of(padapter, memberID));
    		});
    		mono.doOnError(throwable->{
    			orelse.run();
    		});
    	});
    }
}