package everyos.discord.luwu.command.utility;

import java.util.ArrayList;

import com.google.gson.JsonParser;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.UnirestUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.DictionaryCommandHelp, ehelp = LocalizedString.DictionaryCommandExtendedHelp, category=CategoryEnum.Utility)
public class DictionaryCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return UnirestUtil.get("https://aplet123-wordnet-search-v1.p.rapidapi.com/master?word="+argument.toLowerCase(), spec->{
				return spec
					.header("x-rapidapi-host", "aplet123-wordnet-search-v1.p.rapidapi.com")
					.header("x-rapidapi-key", data.bot.rapidKey);
			})
			.map(resp->resp.getBody())
			.flatMap(resp->{
				String defraw = JsonParser.parseString(resp).getAsJsonObject().get("definition").getAsString();
				StringBuilder msg = new StringBuilder("**"+argument+" - Definition**\n");
				Definition[] entries = parse(defraw, argument.toLowerCase());
				if (entries.length==0) return channel.createMessage("I can't find that word!");
				for (Definition entry: entries) {
					msg.append(String.format("(**%s**) %s\n", entry.part, entry.definition));
					if (entry.example!=null&&entry.example.contains(argument)) msg.append("*Example:* "+entry.example+"\n");
					msg.append("\n");
				}
				return channel.createMessage(msg.toString());
			}).onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
		});
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
}

class Definition {
	String part;
	String definition;
	String example;
}

enum ParseState {
	INITIAL, TYPE, SCAN_DEF, DEF, SCAN_EXAMPLE, EXAMPLE
}