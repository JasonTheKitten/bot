package everyos.discord.bot.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.discord.bot.localization.Localization;
import reactor.core.publisher.Mono;

public class TranslateUtil {
	public static Mono<TranslateResult> translate(String key, String text, String target) {
		String url;
		try {
			url = String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s", key, URLEncoder.encode(text, "UTF-8"), target);
		} catch (UnsupportedEncodingException e) { return Mono.error(e); }
		return UnirestUtil.post(url, body->body).flatMap(resp->{
			if (resp.getStatus()<200||resp.getStatus()>=400) return Mono.error(new Exception("Invalid HTTP status"));
			return Mono.just(new TranslateResult(JsonParser.parseString(resp.getBody()).getAsJsonObject()));
		});
	}
	
	public static class TranslateResult {
		public String result;
		public String from;

		public TranslateResult(JsonObject info) {
			result = info.get("text").getAsString();
			from = info.get("lang").getAsString().substring(0, 2);
		}
	}

	public static String locale(Localization locale) {
		switch(locale) {
			case en_US:
				return "en";
			default:
				return "en";
		}
	}
}