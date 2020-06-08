package everyos.discord.luwu.util;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.http.client.ClientException;
import everyos.discord.luwu.localization.LocalizationProvider;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

public class ErrorUtil {
	public static Mono<Message> handleError(Throwable e, MessageChannel channel, LocalizationProvider provider) {
		if (e instanceof LocalizedException) {
			LocalizedException exc = (LocalizedException) e;
			return channel.createMessage(provider.localize(exc.label, exc.fillins));
		} else if (e instanceof EmptyException) {
			return Mono.empty();
		} else if (e instanceof ClientException) {
			if (ClientException.isStatusCode(404).test(e))
				return channel.createMessage(provider.localize(LocalizedString.NotFoundException));
			e.printStackTrace();
			return Mono.empty();
		} else {
			e.printStackTrace();
			return Mono.empty();
		}
	}
	
	public static class LocalizedException extends Exception {
		private static final long serialVersionUID = 5920451886808777972L;
		
		private LocalizedString label;
		private HashMap<String, String> fillins;
		
		public LocalizedException(LocalizedString label) {
			this.label = label;
		}
		public LocalizedException(LocalizedString  label, HashMap<String, String> fillins) {
			this.label = label;
			this.fillins = fillins;
		}
	}
	
	public static class EmptyException extends Exception {
		public EmptyException() {
			super();
		}
		
		private static final long serialVersionUID = -961622066422907583L;
	}
}
