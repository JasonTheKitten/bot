package everyos.nertivia.nertivia4j.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import everyos.nertivia.nertivia4j.entity.channel.PrivateMessageChannel;
import everyos.nertivia.nertivia4j.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class User {
	private long userID;
	private NertiviaClient client;
	private NertiviaInstance instance;
	
	private boolean isBot;

	public User(NertiviaClient client, NertiviaInstance instance, long userID, boolean isBot) {
		this.client = client;
		this.instance = instance;
		this.userID = userID;
		this.isBot = isBot;
	}
	
	public static Mono<User> of(NertiviaClient client, NertiviaInstance instance, long userID) {
		return UnirestUtil.get(NertiviaInstance.REST_ENDPOINT+"/user/"+userID, req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			//System.out.println(resp.getBody());
			JsonObject object = JsonParser.parseString(resp.getBody()).getAsJsonObject();
			return Mono.just(new User(client, instance,
				object.get("user").getAsJsonObject().get("uniqueID").getAsLong(),
				object.get("user").getAsJsonObject().has("bot")));
		});
	}
	
	public NertiviaClient getClient() {return client;}
	public long getID() {return userID;}
	
	public Mono<PrivateMessageChannel> getPrivateMessageChannel() {
		return UnirestUtil.post(NertiviaInstance.REST_ENDPOINT+"/channels/"+userID, req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			JsonObject object = JsonParser.parseString(resp.getBody()).getAsJsonObject();
			return Mono.just(Channel.from(client, instance, object.get("channel").getAsJsonObject())).cast(PrivateMessageChannel.class);
		});
	}

	public Mono<Member> asMember(long serverID) {
		return Mono.just(new Member(client, instance, userID, serverID, isBot));
	}

	public boolean isBot() {
		return isBot;
	}
}
