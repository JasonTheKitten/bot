package everyos.discord.bot.util;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

public class ErrorUtil {
	public static Mono<Message> handleError(Throwable e, MessageChannel channel, LocalizationProvider provider) {
		if (e instanceof LocalizedException) {
			return channel.createMessage(provider.localize(((LocalizedException) e).label));
		} else {
			e.printStackTrace();
			return Mono.empty();
		}
	}
	
	public static class LocalizedException extends Exception {
		private static final long serialVersionUID = 5920451886808777972L;
		
		private LocalizedString label;
		public LocalizedException(LocalizedString label) {
			this.label = label;
		}
	}
}
