package everyos.nertivia.nertivia4j.entity.channel;

import everyos.nertivia.nertivia4j.NertiviaInstance;
import everyos.nertivia.nertivia4j.util.UnirestUtil;
import reactor.core.publisher.Mono;

public interface ServerChannel extends Channel {
	public long getServerID();
	public String getName();
	public Mono<Void> delete();
	
	public static Mono<Void> delete(ServerChannel channel, NertiviaInstance instance) {
		return UnirestUtil.delete(NertiviaInstance.REST_ENDPOINT+"/servers/"+channel.getServerID()+"/channels/"+channel.getID(), req->{
			return req
				.header("authorization", instance.token);
		}).flatMap(resp->{
			//String response = resp.getBody();
			return Mono.empty();
		});
	}
}
