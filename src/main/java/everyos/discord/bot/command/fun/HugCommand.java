package everyos.discord.bot.command.fun;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.HugCommandHelp, ehelp = LocalizedString.HugCommandExtendedHelp, category=CategoryEnum.Fun)
public class HugCommand implements ICommand {
	String[] hugs;
	
	public HugCommand() {
		try {
            InputStream in = ClassLoader.getSystemResourceAsStream("hugs.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            ArrayList<String> hugs = new ArrayList<String>();
            reader.lines().forEach(line->hugs.add(line));
            reader.close();
            this.hugs = hugs.toArray(new String[hugs.size()]);
        } catch (Exception e) { e.printStackTrace(); }
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			User author = message.getAuthor().get();
			Mono<User> mono;	
			ArgumentParser parser = new ArgumentParser(argument);
			if (parser.isEmpty()) {
				mono = Mono.just(author);
			} else if (!parser.couldBeUserID()) {
				return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			} else {
				mono = data.bot.client.getUserById(Snowflake.of(parser.eatUserID())); //TODO: Check user valid (UserUtil)
			}
			
			return mono.flatMap(target->{
				return channel.createEmbed(embed->{
					if (target.getId().equals(author.getId())) {
						embed.setDescription(data.localize(LocalizedString.HugSent));
					} else if (target.getId().equals(Snowflake.of(data.bot.clientID))) {
						embed.setDescription(data.localize(LocalizedString.HugSentBot));
					} else {
						embed.setDescription(data.localize(LocalizedString.HugSentUser, FillinUtil.of(
							"invoker", author.getUsername(), author.getDiscriminator(),
							"recipient", target.getUsername(), target.getDiscriminator())));
					}
					
					String hugURL = hugs[(int) Math.round(Math.random()*(hugs.length-1))];
					embed.setImage(hugURL);
					if (hugURL.contains("giphy.com")) { //Sloppy, will edit later
						embed.setFooter("Powered by Giphy", null); //Not localized, I can't care enough
					} else if (hugURL.contains("tenor.com")) {
						embed.setFooter("Powered by Tenor", null);
					} else {
						embed.setFooter("Powered by Unknown Image Provider", null);
					}
				});
			});
		});
	}
}
