package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.fun.GiphyCommand;
import everyos.discord.bot.command.fun.HugCommand;
import everyos.discord.bot.command.info.InfoCommand;
import everyos.discord.bot.command.info.PingCommand;
import everyos.discord.bot.command.info.ProfileCommand;
import everyos.discord.bot.command.info.UptimeCommand;
import everyos.discord.bot.command.moderation.PurgeCommand;
import everyos.discord.bot.command.utility.GiveawayCommand;
import everyos.discord.bot.command.utility.SuggestCommand;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class DMChannelCase implements IGroupCommand {
	private HashMap<String, ICommand> commands;
	public DMChannelCase() {
		commands = new HashMap<String, ICommand>();
		commands.put("ping", new PingCommand());
        commands.put("uptime", new UptimeCommand());
        commands.put("suggest", new SuggestCommand());
        commands.put("profile", new ProfileCommand());
        commands.put("hug", new HugCommand());
        commands.put("giphy", new GiphyCommand());
        commands.put("purge", new PurgeCommand());
        commands.put("info", new InfoCommand());
        commands.put("giveaway", new GiveawayCommand());
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		String content = message.getContent().orElse("");
        String trunc = ArgumentParser.getIfPrefix(content, 
            new String[] {"--- ", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"});
        if (trunc == null) return Mono.empty();
        String command = ArgumentParser.getCommand(trunc);
        String arg = ArgumentParser.getArgument(trunc);

        if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        return Mono.empty();
	}
}
