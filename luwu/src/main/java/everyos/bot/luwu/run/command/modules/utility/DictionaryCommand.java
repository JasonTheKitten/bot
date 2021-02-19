package everyos.bot.luwu.run.command.modules.utility;

import java.util.ArrayList;

import com.google.gson.JsonParser;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.UnirestUtil;
import reactor.core.publisher.Mono;

public class DictionaryCommand extends CommandBase {
	public DictionaryCommand() {
		super("command.dictionary", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	/*Salvaged, with modifications, from V3*/
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		String word = parser.getRemaining().toLowerCase();
		
		ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
		@SuppressWarnings("deprecation")
		String rapidKey = data.getClient().getBotEngine().getConfiguration().getCustomField("rapid-key");
		return UnirestUtil.get("https://aplet123-wordnet-search-v1.p.rapidapi.com/master?word="+word, spec->{
			return spec
				.header("x-rapidapi-host", "aplet123-wordnet-search-v1.p.rapidapi.com")
				.header("x-rapidapi-key", rapidKey);
		})
		.map(resp->resp.getBody())
		.flatMap(resp->{
			String defraw = JsonParser.parseString(resp).getAsJsonObject().get("definition").getAsString();
			StringBuilder msg = new StringBuilder("**"+word+" - Definition**\n");
			Definition[] entries = parse(defraw, word);
			if (entries.length==0) return channel.send("I can't find that word!");
			for (Definition entry: entries) {
				msg.append(String.format("(**%s**) %s\n", entry.part, entry.definition));
				if (entry.example!=null&&entry.example.contains(word)) msg.append("*Example:* "+entry.example+"\n");
				msg.append("\n");
			}
			String fin = msg.toString();
			
			/*List<String> definition = new ArrayList<>();
			while (fin.length()>0) {
				if (fin.length()>2000) {
					definition.add(fin.substring(0, 2000));
					fin = fin.substring(2000, fin.length());
				} else {
					definition.add(fin);
					fin = "";
				}
				//TODO: Test error messages
			}
			return Flux.fromArray(definition.toArray(new String[definition.size()]))
				.flatMap(s->channel.send(s))
				.collectList();*/
			
			if (fin.length()>1000) {
				return channel.send(locale.localize("command.dictionary.error.longmessage",
					"url", "http://wordnetweb.princeton.edu/perl/webwn?s="+word));
			} else {
				return channel.send(fin);
			}
		}).onErrorResume(e->{e.printStackTrace(); return Mono.empty();})
		.then();
	}
	
	public Mono<String> parseArguments(ArgumentParser parser, Locale locale) {
		String word = parser.getRemaining().trim().toLowerCase();
		if (word.isEmpty()) {
			return expect(locale, parser, "command.error.string");
		}
		
		return Mono.just(word);
	}
	
	public Definition[] parse(String raw, String target) {
		if (raw.isEmpty()) return new Definition[0];
		
		ArrayList<Definition> entries = new ArrayList<Definition>();
		
		String tp = "\n"+raw+"\nS: ";
		int i = 0; int len = raw.length(); int in = 0;
		ParseState state = ParseState.INITIAL;
		Definition entry = null;
		while (i<len) {
			if (tp.substring(i, i+4).equals("\nS: ")) {
				if (entry!=null) entries.add(entry);
				entry = new Definition();
				i+=4; in = 0;
				state = ParseState.TYPE;
				continue;
			}
			
			switch(state) {
				case INITIAL: i++; break;
				case TYPE:
					if (tp.charAt(i)=='(') {}
					else if (tp.charAt(i)==')') {state = ParseState.SCAN_DEF;}
					else {
						if (entry.part == null) entry.part = "";
						entry.part+=tp.charAt(i);
					}
					i++; break;
				case SCAN_DEF:
					if (tp.charAt(i)=='(') state = ParseState.DEF;
					i++; break;
				case DEF:
					if (tp.charAt(i)==')' &&in==0) {state = ParseState.SCAN_EXAMPLE;}
					else {
						if (tp.charAt(i)==')') in--;
						if (tp.charAt(i)=='(') in++;
						if (entry.definition == null) entry.definition = "";
						entry.definition+=tp.charAt(i);
					}
					i++; break;
				case SCAN_EXAMPLE:
					if (tp.charAt(i)=='"') state = ParseState.EXAMPLE;
					i++; break;
				case EXAMPLE:
					if (tp.charAt(i)=='"') {
						if (entry.example!=null&&entry.example.contains(target)) {
							state = ParseState.INITIAL;
						} else state = ParseState.SCAN_EXAMPLE;
					}
					else {
						if (entry.example == null) entry.example = "";
						entry.example+=tp.charAt(i);
					}
					i++; break;
			}
		}
		entries.add(entry);
		
		return entries.toArray(new Definition[entries.size()]);
	}
	
	private class Definition {
		String part;
		String definition;
		String example;
	}

	private enum ParseState {
		INITIAL, TYPE, SCAN_DEF, DEF, SCAN_EXAMPLE, EXAMPLE
	}
}
