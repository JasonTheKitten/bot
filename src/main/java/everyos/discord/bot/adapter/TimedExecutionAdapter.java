package everyos.discord.bot.adapter;

import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBDocument;
import reactor.core.publisher.Mono;

public class TimedExecutionAdapter implements IAdapter {
	private BotInstance bot;
	private long timerID;

	public TimedExecutionAdapter(BotInstance bot, long reminderID) {
		this.bot = bot;
		this.timerID = reminderID;
	}
	
	@Override public Mono<DBDocument> getDocument() {
		return bot.db.collection("timers").scan().with("tid", timerID).orSet(d->{});
	}

	public static TimedExecutionAdapter of(BotInstance bot, long id) {
		return new TimedExecutionAdapter(bot, id);
	}
}