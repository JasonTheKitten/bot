package everyos.bot.luwu.run.command.modules.fun;

import com.kdotj.simplegiphy.SimpleGiphy;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class GiphyCommand extends CommandBase {
	public GiphyCommand() {
		super("command.giphy");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArguments(parser, locale)
			.flatMap(search->searchGiphy(data.getBotEngine(), search, locale)
				.flatMap(url->sendEmbed(data.getChannel(), search, url, locale)));
	}

	private Mono<String> parseArguments(ArgumentParser parser, Locale locale) {
		String query = parser.getRemaining();
		return Mono.just(query.isEmpty()?"Cat":query);
	}

	@SuppressWarnings("deprecation")
	private Mono<String> searchGiphy(BotEngine engine, String search, Locale locale) {
		String giphyKey = engine.getConfiguration().getCustomField("giphy-key");
		
		return Mono.create(sink->{
			SimpleGiphy.setApiKey(giphyKey);
			String result = SimpleGiphy.getInstance().random(search, "g").getRandomGiphy().getImageUrl();
			sink.success(result);
		});
	}
	
	private Mono<Void> sendEmbed(Channel channel, String search, String url, Locale locale) {
		return channel.getInterface(ChannelTextInterface.class).send(spec->{
			spec.setEmbed(embed->{
				embed.setTitle("Giphy - "+search);
				String burl = url.replaceFirst(".+/media/", "https://i.giphy.com/media/").replace("giphy.gif", "200.gif");
				embed.setImage(burl);
				embed.setFooter("Powered by Giphy"); //TODO: Localize
			});
		})
		.then();
	}
}
