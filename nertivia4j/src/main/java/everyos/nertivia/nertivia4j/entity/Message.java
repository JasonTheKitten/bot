package everyos.nertivia.nertivia4j.entity;

import java.awt.Color;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import everyos.nertivia.nertivia4j.entity.channel.MessageChannel;
import everyos.nertivia.nertivia4j.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class Message {
	private NertiviaClient client;
	private NertiviaInstance instance;
	
	private long messageID;
	private long authorID;
	private long channelID;
	
	private long timestamp;
	private Optional<Color> color;
	private Optional<String> content;

	private Message(NertiviaClient client, NertiviaInstance instance, long channelID, long messageID) {
		this.client = client;
		this.instance = instance;
		this.channelID = channelID;
		this.messageID = messageID;
	}
	
	public long getID() {
		return messageID;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public Optional<Color> getColor() {
		return color;
	}
	
	public long getChannelID() {
		return channelID;
	}
	public Mono<MessageChannel> getChannel() {
		return Channel.of(client, instance, channelID).cast(MessageChannel.class);
	}
	
	public long getAuthorID() {
		return authorID;
	}
	public Mono<User> getAuthor() {
		return User.of(client, instance, authorID);
	}
	
	public Mono<Void> delete() {
		return UnirestUtil.delete(NertiviaInstance.REST_ENDPOINT+"/messages/"+messageID+"/channels/"+channelID, req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			//String response = resp.getBody();
			return Mono.empty();
		});
	}
	
	public static Mono<Message> of(NertiviaClient client, NertiviaInstance instance, long channelID, long messageID) {
		return UnirestUtil.get(NertiviaInstance.REST_ENDPOINT+"/messages/"+messageID+"/channels/"+channelID, req->{
			return req
				.header("authorization", instance.token);
		}).map(resp->{
			String response = resp.getBody();
			return from(client, instance, JsonParser.parseString(response).getAsJsonObject());
		});
	}
	
	public static Message from(NertiviaClient client, NertiviaInstance instance, JsonObject data) {
		Message msg = new Message(client, instance, data.get("channelID").getAsLong(), data.get("messageID").getAsLong());
		
		msg.authorID = data.get("creator").getAsJsonObject().get("uniqueID").getAsLong();
		msg.timestamp = data.get("created").getAsLong();
		msg.color = Optional.ofNullable(data.has("color")?Color.decode(data.get("color").getAsString()):null);
		msg.content = Optional.ofNullable(data.has("message")?data.get("message").getAsString():null);
		
		return msg;
	}

	public Optional<String> getContent() {
		return content;
	}
}
