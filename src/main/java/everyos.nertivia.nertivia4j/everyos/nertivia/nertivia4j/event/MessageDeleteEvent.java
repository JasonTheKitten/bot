package everyos.nertivia.nertivia4j.event;

import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import reactor.core.publisher.Mono;

public class MessageDeleteEvent extends MessageEvent {
	@SuppressWarnings("unused")
	private NertiviaInstance instance;
	
	private long cid;
	private long mid;
	public MessageDeleteEvent(NertiviaClient client, NertiviaInstance instance, JSONObject response) {
		super(client);
		
		this.instance = instance;
		
		JsonObject parsed = JsonParser.parseString(response.toString()).getAsJsonObject();
		this.cid = parsed.get("channelID").getAsLong();
		this.mid = parsed.get("messageID").getAsLong();
	}
	public long getChannelID() {
		return cid;
	}
	public Mono<Channel> getChannel() {
		return getClient().getChannelByID(cid);
	}
	public long getMessageID() {
		return mid;
	}
}
