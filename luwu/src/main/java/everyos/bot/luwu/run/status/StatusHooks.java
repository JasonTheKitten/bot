package everyos.bot.luwu.run.status;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import everyos.bot.chat4j.status.Status;
import everyos.bot.chat4j.status.StatusType;
import everyos.bot.luwu.core.Configuration;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.event.ServerCreateEvent;
import everyos.bot.luwu.core.entity.event.ServerDeleteEvent;
import everyos.bot.luwu.core.entity.event.ServerEvent;
import everyos.bot.luwu.core.entity.event.ServerOutageEvent;
import everyos.bot.luwu.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class StatusHooks {
	
	private static Map<Connection, Integer> counts = new HashMap<>();
	private static Map<Connection, Integer> lastCounts = new HashMap<>();

	public static Mono<Void> statusHook(ServerEvent event) {
		Connection connection = event.getConnection();
		if (event instanceof ServerCreateEvent) {
			counts.put(connection, counts.getOrDefault(connection, 0)+1);
		} else if (event instanceof ServerDeleteEvent) {
			counts.put(connection, counts.getOrDefault(connection, 0)-1);
		} else if (event instanceof ServerOutageEvent) {
			counts.put(connection, counts.getOrDefault(connection, 0)-1);
		}

		return Mono.empty();
    }

	public static Mono<Void> statusUpdateHook(Connection connection) {
		int count = counts.getOrDefault(connection, 0);
		if (count!=lastCounts.getOrDefault(connection, 0)) {
			lastCounts.put(connection, count);
			return onRecount(connection, count);
		}

		return Mono.empty();
	}
    
    @SuppressWarnings("deprecation")
    private static Mono<Void> onRecount(Connection connection, int c) {
        Mono<Void> m1 = connection.setStatus(new Status[] {
            new Status(StatusType.WATCHING, c + " server" + (c != 1 ? "s" : "") + " | luwu help | Luwu!")
        });
        
        Configuration configs = connection.getBotEngine().getConfiguration();
		if (configs.getCustomField("debug").equals("true")) {
			return m1;
		}
		
		Mono<Void> m2 = UnirestUtil.post("https://botblock.org/api/count", req->{
			JsonObject body = new JsonObject();
			body.addProperty("server_count", c);
			body.addProperty("bot_id", String.valueOf(connection.getSelfID()));
			body.addProperty("discord.bots.gg", configs.getCustomField("botsgg_key"));
			body.addProperty("bots.ondiscord.xyz", configs.getCustomField("bod_key"));
			body.addProperty("discordbotlist.com", configs.getCustomField("dblcom_key"));
			body.addProperty("top.gg", configs.getCustomField("topgg_key"));
			
			return req
				.header("Content-Type", "application/json")
				.body(body.toString());
		}).then();

		return m1.and(m2);
    }
    
}
