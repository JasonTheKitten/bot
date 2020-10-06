package everyos.nertivia.nertivia4j.event;

import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.entity.Message;

public class MessageCreateEvent extends MessageEvent {
	private Message message;

	public MessageCreateEvent(NertiviaClient client, NertiviaInstance instance, JSONObject response) {
		super(client);
		JsonObject parsed = JsonParser.parseString(response.toString()).getAsJsonObject();
		this.message = Message.from(client, instance, parsed.get("message").getAsJsonObject());
	}
	public Message getMessage() {
		return message;
	}
}
