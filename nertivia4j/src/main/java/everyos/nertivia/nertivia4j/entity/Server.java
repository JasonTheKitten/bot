package everyos.nertivia.nertivia4j.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import everyos.nertivia.nertivia4j.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class Server {
	private NertiviaInstance instance;
	private long guildID;
	private long primaryChannelID;
	private NertiviaClient client;

	private Server(NertiviaClient client, NertiviaInstance instance, long guildID) {
		this.client = client;
		this.instance = instance;
		this.guildID = guildID;
	}
	
	public static Mono<Server> of(NertiviaClient client, NertiviaInstance instance, long gid) {
		return UnirestUtil.get(NertiviaInstance.REST_ENDPOINT+"/servers/"+gid, req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			JsonObject response = JsonParser.parseString(resp.getBody()).getAsJsonObject();
			Server server = new Server(client, instance, gid);
			server.primaryChannelID = response.get("default_channel_id").getAsLong();
			return Mono.just(server);
		});
	}
	
	public Mono<Void> delete() {
		return UnirestUtil.delete(NertiviaInstance.REST_ENDPOINT+"/servers/"+guildID, req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			return Mono.empty();
		});
	}
	
	public long getDefaultChannelID() {
		return primaryChannelID;
	}
	public Mono<Channel> getDefaultChannel() {
		return client.getChannelByID(primaryChannelID);
	}
	
	public Mono<Channel[]> getChannels() {
		return UnirestUtil.get(NertiviaInstance.REST_ENDPOINT+"/servers/"+guildID+"/channels", req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			JsonArray response = JsonParser.parseString(resp.getBody()).getAsJsonArray();
			Channel[] channels = new Channel[response.size()];
			for (int i=0; i<response.size(); i++) {
				channels[i] = Channel.from(client, instance, response.get(i).getAsJsonObject());
			}
			return Mono.just(channels);
		});
	}
}
