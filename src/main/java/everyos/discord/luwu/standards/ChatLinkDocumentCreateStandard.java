package everyos.discord.luwu.standards;

import everyos.discord.luwu.BotInstance;
import reactor.core.publisher.Mono;

public class ChatLinkDocumentCreateStandard {
	public static Mono<Boolean> exists(BotInstance bot, long id) {
		return bot.db.collection("chatlinks").scan().with("clid", id).exists();
	}
}
