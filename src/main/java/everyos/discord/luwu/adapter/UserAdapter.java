package everyos.discord.luwu.adapter;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.database.DBDocument;
import reactor.core.publisher.Mono;

public class UserAdapter implements IAdapter {
    private BotInstance instance;
    private long id;

    public UserAdapter(BotInstance bot, long id) {
        this.instance = bot;
        this.id = id;
    }

    public static UserAdapter of(BotInstance bot, long user) {
		return new UserAdapter(bot, user);
	}
    
    public Mono<User> getMember() {
    	return instance.client.getUserById(Snowflake.of(id));
    }
    public long getUserID() {
		return id;
	}
    
    public Mono<? extends Object> wipe() {
		return instance.db.collection("members").scan().with("uid", id).deleteAll()
			.then(instance.db.collection("messages").scan().with("uid", id).deleteAll())
			.then(instance.db.collection("users").scan().with("uid", id).deleteAll())
			.then(instance.db.collection("guilds").scan().with("uid", id).deleteAll());
	}

    @Override public Mono<DBDocument> getDocument() {
		return instance.db.collection("users").scan().with("uid", id).orSet(doc->{});
	}
}
