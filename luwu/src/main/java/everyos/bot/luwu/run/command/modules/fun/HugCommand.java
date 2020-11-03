package everyos.bot.luwu.run.command.modules.fun;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
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

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//TODO: Segregate argument parser from command: allows for running commands from more UIs
		
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
			.flatMap(member->determineHugMessage(data.getClient(), invoker, member, locale))
			.flatMap(message->sendHugMessage(data.getChannel(), message, locale))
			//TODO: Send the hug
			.then();
	}

	private Mono<Long> parseArgs(ArgumentParser parser, UserID userID, Locale locale) {
		//TODO: Method return type should be a UserID
		//Parse arguments
		if (parser.isEmpty()) return Mono.just(userID.getLong());
		if (!parser.couldBeUserID()) return Mono.error(new TextException("Usage")); //TODO
		return Mono.just(parser.eatUserID());
	}
	private Mono<String> determineHugMessage(Client client, User invoker, User member, Locale locale) {
		//Choose the header
		if (invoker.getID()==member.getID()) {
			return Mono.just(locale.localize("command.hug.selfhug"));
		} else {
			return client.getSelfAsUser()
				.filter(botmember->botmember.getID().getLong()==member.getID().getLong())
				.flatMap(v->Mono.just(locale.localize("command.hug.bothug")))
				.switchIfEmpty(Mono.just(locale.localize("command.hug.userhug", "invoker", formatName(invoker), "recipient", formatName(member))));
		}
	}
	
	private Mono<Void> sendHugMessage(Channel channel, String message, Locale locale) {
		ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
		return textGrip.send(spec->{
			spec.setContent(message);
			spec.addAttachment("hug.gif", hugs[(int) (Math.round(Math.random()*hugs.length))]);
			//TODO: Footer
		})
		.then();
	}

	private String formatName(User user) {
		return user.getHumanReadableID();
	}
}
