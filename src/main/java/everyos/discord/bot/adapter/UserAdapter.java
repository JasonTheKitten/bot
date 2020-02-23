package everyos.discord.bot.adapter;

import java.util.function.Consumer;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.standards.UserDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBCollection;
import everyos.storage.database.DBDocument;

public class UserAdapter implements IAdapter {
    String uid;
    User user;

    public UserAdapter(User user) {
    	this(user.getId().asString());
        this.user = user;
    }
    public UserAdapter(String uid) {
        this.uid = uid;
    }
    
    public void require(Consumer<UserAdapter> func) {
    	if (this.user==null) {
    		Main.client.getUserById(Snowflake.of(uid)).subscribe(user->{
    			this.user = user;
    			func.accept(this);
    		});
    		return;
    	}
		func.accept(this);
	}

    public void getUsername(Consumer<String> func) {
    	require(uadapter->{
    		func.accept(user.getUsername());
    	});
    }
    
    @Override public DBDocument getDocument() {
        return Main.db.collection("users").getOrSet(uid, UserDocumentCreateStandard.standard);
    }

    public static UserAdapter of(String uid) {
        ObjectStore rtn = new ObjectStore();
        DBCollection collection = Main.db.collection("users");
        collection.getIfPresent(uid, channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = collection.getOrSet(uid, UserDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new UserAdapter(uid);});
        });

        return (UserAdapter) rtn.object;
    }
}