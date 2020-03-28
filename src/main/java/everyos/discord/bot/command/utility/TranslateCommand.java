package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class TranslateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return null;
		});
	}
	
	public Mono<?> translate(String key, String text, String targets) {
		return UnirestUtil.post("https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to="+targets, body->{
			return body
				.header("Content-Type", "application/json")
				.header("Ocp-Apim-Subscription-Key", key);
		});
	}
	
	public class TranslationResult {}
}
