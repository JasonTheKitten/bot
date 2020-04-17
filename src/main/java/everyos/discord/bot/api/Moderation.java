package everyos.discord.bot.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.discord.bot.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class Moderation {
	public static Mono<Boolean> isImageSafe(String key, String url) {
		return UnirestUtil.get(String.format("https://www.moderatecontent.com/api/v2?key=%s&url=%s", key, url), p->p).map(resp->{
			JsonObject object = JsonParser.parseString(resp.getBody()).getAsJsonObject();
			return !(object.has("rating_letter")&&object.get("rating_letter").getAsString().equals("a"));
			//If the object does not have "rating_letter", it's most likely not even an image
		});
	}
}
