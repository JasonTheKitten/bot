package everyos.discord.bot.event;

import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.command.channel.StarboardCommand;
import everyos.discord.bot.database.DBObject;
import reactor.core.publisher.Mono;

public class ReactionRemoveEventHandler {
	private BotInstance bot;

	public ReactionRemoveEventHandler(BotInstance bot) {
		this.bot = bot;
	}

	public Mono<?> handle(ReactionRemoveEvent e) {
		return e.getMessage().flatMap(message->{	
			String emoji =
				e.getEmoji().asUnicodeEmoji().map(ue->ue.getRaw())
				.orElseGet(()->e.getEmoji().asCustomEmoji().map(ce->ce.getId().asString()).get());
			
			//Reaction role code
			Mono<?> m1 = MessageAdapter.of(bot, message.getChannelId().asLong(), message.getId().asLong()).getDocument().map(doc->{
				if (!e.getGuildId().isPresent()) return Mono.empty();
				return e.getUser().flatMap(user->user.asMember(e.getGuildId().get())).flatMap(member->{
					String roleID = doc.getObject().getOrDefaultObject("roles", new DBObject()).getOrDefaultString(emoji, null);
					if (roleID==null) return Mono.empty();
					return member.removeRole(Snowflake.of(roleID));
				});
			});
			
			
			Mono<?> m2 = StarboardCommand.proccessStarboardReactions(bot, message, e.getEmoji());
			
			return Mono.when(m1, m2).onErrorContinue((ex, o)->{
				if (!ClientException.isStatusCode(404).test(ex)) ex.printStackTrace();
			});
		})
		.onErrorContinue((err, o)->err.printStackTrace());
	}
}