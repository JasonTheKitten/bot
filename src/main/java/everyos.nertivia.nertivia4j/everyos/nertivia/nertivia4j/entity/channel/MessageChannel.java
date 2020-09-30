package everyos.nertivia.nertivia4j.entity.channel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.Message;
import everyos.nertivia.nertivia4j.util.UnirestUtil;
import reactor.core.publisher.Mono;

public interface MessageChannel extends Channel {
	public Mono<Message> send(String message);
	
	public static Mono<Message> send(MessageChannel channel, NertiviaInstance instance, String content) {
		return UnirestUtil.post(NertiviaInstance.REST_ENDPOINT+"/messages/channels/"+channel.getID(), req->{
			JsonObject obj = new JsonObject();
			obj.addProperty("message", content);
			return req
				.header("authorization", instance.token)
				.header("Content-Type", "application/json")
				.body(obj.toString());
		}).map(resp->{
			JsonObject object = JsonParser.parseString(resp.getBody()).getAsJsonObject().get("messageCreated").getAsJsonObject();
			return Message.from(channel.getClient(), instance, object);
		});
	}
}
