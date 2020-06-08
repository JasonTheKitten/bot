package everyos.discord.luwu.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.command.fun.GiphyCommand;
import everyos.discord.luwu.command.fun.HugCommand;
import everyos.discord.luwu.command.info.HelpCommand;
import everyos.discord.luwu.command.info.InfoCommand;
import everyos.discord.luwu.command.info.PingCommand;
import everyos.discord.luwu.command.info.ProfileCommand;
import everyos.discord.luwu.command.info.UptimeCommand;
import everyos.discord.luwu.command.moderation.PurgeCommand;
import everyos.discord.luwu.command.utility.GiveawayCommand;
import everyos.discord.luwu.command.utility.SuggestCommand;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.parser.ArgumentParser;
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
        commands.put("help", new HelpCommand());
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		String content = message.getContent();
        String trunc = ArgumentParser.getIfPrefix(content, data.prefixes);
        if (trunc == null) return Mono.empty();
        String command = ArgumentParser.getCommand(trunc);
        String arg = ArgumentParser.getArgument(trunc);

        if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        return Mono.empty();
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}
