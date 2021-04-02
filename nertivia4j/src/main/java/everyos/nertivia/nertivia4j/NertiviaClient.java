package everyos.nertivia.nertivia4j;

import java.net.URISyntaxException;

import org.json.JSONObject;

import everyos.nertivia.nertivia4j.entity.Message;
import everyos.nertivia.nertivia4j.entity.Server;
import everyos.nertivia.nertivia4j.entity.User;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import everyos.nertivia.nertivia4j.event.Event;
import everyos.nertivia.nertivia4j.event.MessageCreateEvent;
import everyos.nertivia.nertivia4j.event.MessageDeleteEvent;
import everyos.nertivia.nertivia4j.event.ReadyEvent;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.core.publisher.Sinks.Many;
import reactor.util.Logger;
import reactor.util.Loggers;

public class NertiviaClient {
	private NertiviaClientOptions options;
	private Logger logger;
	

	public NertiviaClient(NertiviaClientOptions options) {
		this.options = options;
		this.logger = Loggers.getLogger(NertiviaClient.class);
	}
	
	public static class NertiviaClientOptions {
		int shard;
		public NertiviaInstance instance;
		
		public void setInstance(NertiviaInstance instance) {
			this.instance = instance;
		}
	}
	
	public Mono<NertiviaConnection> connect() {
		try {
			Many<Event> sink = Sinks.many().multicast().<Event>onBackpressureBuffer();
			EmitFailureHandler handler = (signalType, emitResult)->{
				logger.debug("Failed to push an event!");
				return false;
			};
			
			IO.Options socketOptions = new IO.Options();
			socketOptions.transports = new String[] {WebSocket.NAME};
			Socket socket = IO.socket(NertiviaInstance.SOCKET_URL, socketOptions);
			socket.on(Socket.EVENT_CONNECT, e->{
				info("Connected to socket - Authenticating", false);
				JSONObject auth = new JSONObject();
				auth.put("token", options.instance.token);
				socket.emit("authentication", auth);
			});
			socket.on("success", e->{
				info("Successfully authenticated - Ready", false);
				sink.emitNext(new ReadyEvent(this), handler);
			});
			socket.on("receiveMessage", e->{
				JSONObject response = (JSONObject) e[0];
				sink.emitNext(new MessageCreateEvent(this, options.instance, response), handler);
			});
			socket.on("delete_message", e->{
				JSONObject response = (JSONObject) e[0];
				sink.emitNext(new MessageDeleteEvent(this, options.instance, response), handler);
			});
			socket.on(Socket.EVENT_DISCONNECT, e->{
				info("Disconnected from socket", false);
			});
			socket.on(Socket.EVENT_CONNECT_ERROR, e->{
				info((e[0]).toString(), true);
			});
			socket.connect();
			return Mono.just(new NertiviaConnection() {
				@Override public Mono<Void> logout() {
					return Mono.just(true).doOnNext(b->{
						//sink.complete();
						socket.close();
					}).then();
				}
				
				@Override public <T extends Event> Flux<T> listen(Class<T> e) {
					return sink.asFlux().filter(o->e.isInstance(o)).cast(e);
					//return emitter.filter(o->e.isInstance(o)).cast(e);
				}
			});
		} catch (URISyntaxException e) {
			return Mono.error(e);
		}
	}
	
	private void info(String string, boolean error) {
		String cls = this.toString();
		cls = cls.substring(cls.indexOf("@")+1);
		if (error) {
			logger.info("[C:"+cls+"] "+string);
		} else {
			logger.info("[C:"+cls+"] "+string);
		}
	}
	
	public Mono<Channel> getChannelByID(long cid) {
		return Channel.of(this, options.instance, cid);
	}

	public Mono<Server> getServerByID(long gid) {
		return Server.of(this, options.instance, gid);
	}
	
	public Mono<Message> getMessageByID(long cid, long mid) {
		return Message.of(this, options.instance, cid, mid);
	}
	
	public Mono<User> getUserByID(long uid) {
		return User.of(this, options.instance, uid);
	}

	public Mono<User> getSelfAsUser() {
		// TODO
		return Mono.just(new User(this, options.instance, -1L, true));
	}
}