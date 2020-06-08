package everyos.discord.luwu.util;

import java.util.function.Function;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import reactor.core.publisher.Mono;

public class UnirestUtil {
	public static Mono<HttpResponse<String>> get(String URL, Function<GetRequest, BaseRequest> func) {
		return Mono.create(sink->{
			func.apply(Unirest.get(URL)).asStringAsync(new Callback<String>() {
				@Override public void completed(HttpResponse<String> response) {
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

	public static Mono<HttpResponse<String>> post(String URL, Function<HttpRequestWithBody, BaseRequest> func) {
		return Mono.create(sink->{
			func.apply(Unirest.post(URL)).asStringAsync(new Callback<String>() {
				@Override public void completed(HttpResponse<String> response) {
					if (response.getStatus()>=200&&response.getStatus()<400) {
						sink.success(response);
					} else {
						sink.error(new Exception(response.getBody()));
					}
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
	
	public static Mono<HttpResponse<String>> delete(String URL, Function<HttpRequestWithBody, BaseRequest> func) {
		return Mono.create(sink->{
			func.apply(Unirest.delete(URL)).asStringAsync(new Callback<String>() {
				@Override public void completed(HttpResponse<String> response) {
					if (response.getStatus()>=200&&response.getStatus()<400) {
						sink.success(response);
					} else {
						sink.error(new Exception(response.getBody()));
					}
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
