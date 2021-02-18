package everyos.bot.luwu.util;

import java.util.function.Function;

import kong.unirest.Callback;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import reactor.core.publisher.Mono;

/*Salvaged this from V3*/
public class UnirestUtil {
	public static Mono<HttpResponse<String>> get(String URL, Function<GetRequest, HttpRequest<?>> func) {
		return Mono.create(sink->{
			func.apply(Unirest.get(URL)).asStringAsync(new Callback<String>() {
				@Override
				public void completed(HttpResponse<String> response) {
					sink.success(response);
				}
				
				@Override
				public void failed(UnirestException e) {
					sink.error(e);
				}
				
				@Override
				public void cancelled() {
					sink.success();
				}
			});
		});
	}

	public static Mono<HttpResponse<String>> post(String URL, Function<HttpRequestWithBody, HttpRequest<?>> func) {
		return Mono.create(sink->{
			func.apply(Unirest.post(URL)).asStringAsync(new Callback<String>() {
				@Override
				public void completed(HttpResponse<String> response) {
					if (response.getStatus()>=200&&response.getStatus()<400) {
						sink.success(response);
					} else {
						sink.error(new Exception(response.getBody()));
					}
				}
				
				@Override
				public void failed(UnirestException e) {
					sink.error(e);
				}
				@Override public void cancelled() {
					sink.success();
				}
			});
		});
	}
	
	public static Mono<HttpResponse<String>> delete(String URL, Function<HttpRequestWithBody, HttpRequest<?>> func) {
		return Mono.create(sink->{
			func.apply(Unirest.delete(URL)).asStringAsync(new Callback<String>() {
				@Override public void completed(HttpResponse<String> response) {
					if (response.getStatus()>=200&&response.getStatus()<400) {
						sink.success(response);
					} else {
						sink.error(new Exception(response.getBody()));
					}
				}
				
				@Override
				public void failed(UnirestException e) {
					sink.error(e);
				}
				
				@Override
				public void cancelled() {
					sink.success();
				}
			});
		});
	}
}
