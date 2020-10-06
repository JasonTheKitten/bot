package everyos.bot.luwu.command.modules.fun;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.entity.Client;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.entity.User;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class HugCommand implements Command {
	private static String[] hugs;

	static {
		try {
            InputStream in = ClassLoader.getSystemResourceAsStream("hugs.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            ArrayList<String> mhugs = new ArrayList<String>();
            reader.lines().forEach(line->mhugs.add(line));
            reader.close();
            hugs = mhugs.toArray(new String[mhugs.size()]);
        } catch (Exception e) { e.printStackTrace(); }
	}

	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		//Parse arguments
		//Choose the header
		//Choose a random hug
		//Determine the footer
		//Send an embed or message
		
		Locale locale = data.getLocale();
		Member invoker = data.getInvoker();
		
		return
			parseArgs(parser, invoker.getID(), locale)
			.flatMap(id->data.getConnection().getUserByID(id))
			.flatMap(member->determineHugMessage(data.getClient(), invoker, member, locale));
	}

	private Mono<Long> parseArgs(ArgumentParser parser, long defaultID, Locale locale) {
		//Parse arguments
		return null;
	}
	private Mono<String> determineHugMessage(Client client, User invoker, User member, Locale locale) {
		//Choose the header
		if (invoker.getID()==member.getID()) {
			return Mono.just(locale.localize("command.hug.selfhug"));
		} else {
			return client.getSelfAsUser().flatMap(botmember->{
				if (botmember.getID()==member.getID()) {
					return Mono.just(locale.localize("command.hug.bothug"));
				}
				return Mono.just(locale.localize("command.hug.userhug", "invoker", formatName(invoker), "recipient", formatName(member)));
			});
		}
	}

	private String formatName(User user) {
		return user.getHumanReadableID();
	}
}
