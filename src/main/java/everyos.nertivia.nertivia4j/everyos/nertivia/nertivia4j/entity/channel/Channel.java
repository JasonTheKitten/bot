package everyos.nertivia.nertivia4j.entity.channel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.Message;
import everyos.nertivia.nertivia4j.util.UnirestUtil;
import reactor.core.publisher.Mono;

public interface Channel {
	public NertiviaClient getClient();
	public long getID();
	public Mono<Void> type();

	public static Mono<Channel> of(NertiviaClient client, NertiviaInstance instance, long cid) {
		return UnirestUtil.get(NertiviaInstance.REST_ENDPOINT+"/channels/"+cid, req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			JsonObject response = JsonParser.parseString(resp.getBody()).getAsJsonObject();	
			return Mono.just(from(client, instance, response));
		});
	}
	
	static Channel from(NertiviaClient client, NertiviaInstance instance, JsonObject response) {
		if (response.has("server_id")) {
			return new ServerMessageChannel() {
				@Override public Mono<Void> delete() {
					return ServerChannel.delete(this, instance);
				}

				@Override public Mono<Message> send(String message) {
					return MessageChannel.send(this, instance, message);
				}

				@Override public long getServerID() { return response.get("serverID").getAsLong(); }
				@Override public long getID() { return response.get("channelID").getAsLong(); }
				@Override public String getName() { return response.get("name").getAsString(); }
				@Override public NertiviaClient getClient() { return client; }
				@Override public Mono<Void> type() { return Channel.type(this, client, instance); }
			};
		} else {
			return new PrivateMessageChannel() {
				@Override public Mono<Message> send(String message) {
					return MessageChannel.send(this, instance, message);
				}

				@Override public long getID() { return response.get("channelID").getAsLong(); }
				@Override public NertiviaClient getClient() { return client; }
				@Override public Mono<Void> type() { return Channel.type(this, client, instance); }
			};
		}
	}
	
	static Mono<Void> type(Channel channel, NertiviaClient client, NertiviaInstance instance) {
		return UnirestUtil.post(NertiviaInstance.REST_ENDPOINT+"/messages/"+channel.getID()+"/typing", req->{
			return req
				.header("authorization", instance.token);
		}).then();
	}
}