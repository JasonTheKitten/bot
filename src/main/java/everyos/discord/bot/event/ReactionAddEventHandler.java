package everyos.discord.bot.event;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.command.channel.StarboardCommand;
import everyos.discord.bot.database.DBObject;
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
			Mono<?> m1 = MessageAdapter.of(bot, message.getChannelId().asLong(), message.getId().asLong()).getDocument().map(doc->{
				String roleID = doc.getObject().getOrDefaultObject("roles", new DBObject()).getOrDefaultString(emoji, null);
				if (roleID==null) return Mono.empty();
				return e.getMember().isPresent()?e.getMember().get().addRole(Snowflake.of(roleID)):Mono.empty();
			});
			
			
			Mono<?> m2 = StarboardCommand.proccessStarboardReactions(bot, message, e.getEmoji());
			
			return Mono.when(m1, m2).onErrorContinue((ex, o)->{
				if (!ClientException.isStatusCode(404).test(ex)) ex.printStackTrace();
			});
		})
		.onErrorContinue((err, o)->err.printStackTrace());
	}
}