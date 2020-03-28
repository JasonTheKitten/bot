package everyos.discord.bot.util;

import java.util.function.Function;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import reactor.core.publisher.Mono;

public class UnirestUtil {
	public static Mono<HttpResponse<JsonNode>> get(String URL, Function<GetRequest, GetRequest> func) {
		return Mono.create(sink->{
			func.apply(Unirest.get(URL)).asJsonAsync(new Callback<JsonNode>() {
				@Override public void completed(HttpResponse<JsonNode> response) {
					sink.success(response);
				}
				@Override public void failed(UnirestException e) {
					sink.error(e);
				}
				@Override public void cancelled() {
					sink.success();
				}
			});
		});
	}

	public static Mono<?> post(String URL, Function<HttpRequestWithBody, HttpRequestWithBody> func) {
		return Mono.create(sink->{
			func.apply(Unirest.post(URL)).asJsonAsync(new Callback<JsonNode>() {
				@Override public void completed(HttpResponse<JsonNode> response) {
					sink.success(response);
				}
				@Override public void failed(UnirestException e) {
					sink.error(e);
				}
				@Override public void cancelled() {
					sink.success();
				}
			});
		});
	}
}
