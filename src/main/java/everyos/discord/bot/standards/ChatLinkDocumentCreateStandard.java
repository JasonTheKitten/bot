package everyos.discord.bot.standards;

import everyos.discord.bot.BotInstance;

public class ChatLinkDocumentCreateStandard {
	public static boolean exists(BotInstance bot, long id) {
		return bot.db.collection("chatlinks").has(id);
	}
}
