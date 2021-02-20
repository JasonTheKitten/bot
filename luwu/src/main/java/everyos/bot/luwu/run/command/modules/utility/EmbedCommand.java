package everyos.bot.luwu.run.command.modules.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class EmbedCommand extends CommandBase {
	public EmbedCommand() {
		super("command.embed", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS,
			ChatPermission.SEND_EMBEDS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		return parse(parser, locale).flatMap(parsed->sendEmbed(data.getChannel(), data.getMessage(), parsed));
	}

	private Mono<EmbedDetails> parse(ArgumentParser parser, Locale locale) {
		EmbedDetails details = new EmbedDetails();
		while (!parser.isEmpty()) {
			String flag = parser.eat();
			if (flag.equals("-t")||flag.equals("--title")) {
				if (!parser.couldBeQuote()) return expect(locale, parser, locale.localize("command.error.quote"));
				details.setTitle(parser.eatQuote());
			} else if (flag.equals("-d")||flag.equals("--desc")||flag.equals("--description")) {
				if (!parser.couldBeQuote()) return expect(locale, parser, locale.localize("command.error.quote"));
				details.setDescription(parser.eatQuote());
			} else if (flag.equals("-f")||flag.equals("--field")) {
				if (!parser.couldBeQuote()) return expect(locale, parser, locale.localize("command.error.quote"));
				String t1 = parser.eatQuote();
				if (!parser.couldBeQuote()) return expect(locale, parser, locale.localize("command.error.quote"));
				String t2 = parser.eatQuote();
				details.addField(t1, t2);
			} else if (flag.equals("-i")||flag.equals("--image")) {
				if (!parser.couldBeURL()) return expect(locale, parser, locale.localize("command.error.url"));
				String imageURL = parser.eatURL();
				details.setImage(imageURL);
			}
		}
		
		return Mono.just(details);
	}
	
	private Mono<Void> sendEmbed(Channel channel, Message message, EmbedDetails parsed) {
		return channel.getInterface(ChannelTextInterface.class).send(spec->{
			spec.setEmbed(embed->{
				//embed.setAuthor(); //TODO
				if (parsed.getTitle().isPresent()) embed.setTitle(parsed.getTitle().get());
				if (parsed.getDescription().isPresent()) embed.setDescription(parsed.getDescription().get());
				for (Tuple<String, String> field: parsed.getFields()) {
					embed.addField(field.getT1(), field.getT2(), false);
				};
				if (parsed.getImageURL().isPresent()) {
					embed.setImage(parsed.getImageURL().get());
				}
				embed.setFooter("CUSTOM EMBED\nUser ID: "+message.getAuthorID().toString()); //TODO: Localize
			});
		}).then();
	}
	
	private static class EmbedDetails {
		private String title;
		private String description;
		private List<Tuple<String, String>> fields = new ArrayList<>();
		private String imageURL;
		
		public void setTitle(String title) {
			this.title = title;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		public void addField(String field, String desc) {
			fields.add(Tuple.of(field, desc));
		}
		
		public void setImage(String imageURL) {
			this.imageURL = imageURL;
		}

		public Optional<String> getTitle() {
			return Optional.ofNullable(title);
		}
		
		public Optional<String> getDescription() {
			return Optional.ofNullable(description);
		}
		
		@SuppressWarnings("unchecked")
		public Tuple<String, String>[] getFields() {
			return fields.toArray(new Tuple[fields.size()]);
		}
		
		public Optional<String> getImageURL() {
			return Optional.ofNullable(imageURL);
		}
	}
}
