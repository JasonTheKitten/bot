package everyos.discord.luwu.event;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import everyos.discord.luwu.BotData.ReactionTrigger;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.adapter.MessageAdapter;
import everyos.discord.luwu.command.channel.StarboardCommand;
import everyos.discord.luwu.database.DBObject;
import reactor.core.publisher.Mono;

public class ReactionAddEventHandler {
	private BotInstance bot;

	public ReactionAddEventHandler(BotInstance bot) {
		this.bot = bot;
	}

	public Mono<?> handle(ReactionAddEvent e) {
		return e.getMessage().flatMap(message->{
			String emoji =
				e.getEmoji().asUnicodeEmoji().map(ue->ue.getRaw())
				.orElseGet(()->e.getEmoji().asCustomEmoji().map(ce->ce.getId().asString()).get());
			
			//Reaction role code
			Mono<?> m1 = MessageAdapter.of(bot, message.getChannelId().asLong(), message.getId().asLong()).getDocument().flatMap(doc->{
				long roleID = doc.getObject().getOrDefaultObject("roles", new DBObject()).getOrDefaultLong(emoji, -1L);
				if (roleID==-1L) return Mono.empty();
				return e.getMember().isPresent()&&!e.getMember().get().isBot()?e.getMember().get().addRole(Snowflake.of(roleID)):Mono.empty();
			});
			
			
			//Starboard code
			Mono<?> m2 = StarboardCommand.proccessStarboardReactions(bot, message, e.getEmoji());
			
			//Dynamic reaction code
			Mono<?> m3 = e.getEmoji().asUnicodeEmoji().isPresent()?bot.data.triggers.get(new ReactionTrigger(
				e.getChannelId().asLong(),
				e.getMessageId().asLong(),
				e.getEmoji().asUnicodeEmoji().get().getRaw())):Mono.empty();
			
			return e.getUser().flatMap(user->{
				if (user.isBot()) return Mono.empty();
				return Mono.when(m1, m2, m3==null?Mono.empty():m3).doOnError(ex->{
					ex.printStackTrace();
					//if (!ClientException.isStatusCode(404).test(ex)) ex.printStackTrace();
				});
			});
		})
		.onErrorContinue((err, o)->err.printStackTrace());
	}
}