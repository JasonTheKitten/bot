package everyos.discord.luwu.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.discord.luwu.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class Moderation {
	public static Mono<Boolean> isImageSafe(String key, String url) {
		return UnirestUtil.get(String.format("https://www.moderatecontent.com/api/v2?key=%s&url=%s", key, url), p->p).map(resp->{
			JsonObject object = JsonParser.parseString(resp.getBody()).getAsJsonObject();
			return !(object.has("predictions")&&object.get("predictions").getAsJsonObject().get("adult").getAsFloat()>10);
			//If the object does not have "predictions", it's most likely not even an image
		});
	}
}
