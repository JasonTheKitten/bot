package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.channel.ChatLinkCommand;
import everyos.discord.bot.command.channel.SuggestionsCommand;
import everyos.discord.bot.command.fun.CurrencyCommand;
import everyos.discord.bot.command.info.PingCommand;
import everyos.discord.bot.command.info.ProfileCommand;
import everyos.discord.bot.command.info.UptimeCommand;
import everyos.discord.bot.command.moderation.BanCommand;
import everyos.discord.bot.command.moderation.KickCommand;
import everyos.discord.bot.command.utility.SuggestCommand;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class DefaultChannelCase implements ICommand {
    private HashMap<String, ICommand> commands;
    public DefaultChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("ping", new PingCommand());
        commands.put("uptime", new UptimeCommand());
        commands.put("suggest", new SuggestCommand());
        commands.put("suggestions", new SuggestionsCommand());
        commands.put("link", new ChatLinkCommand());
        commands.put("feth", new CurrencyCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
        commands.put("profile", new ProfileCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent().orElse("");
        String trunc = ArgumentParser.getIfPrefix(content, 
            new String[]{"---", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"});
        if (trunc == null) return Mono.empty();
        String command = ArgumentParser.getCommand(trunc);
        String arg = ArgumentParser.getArgument(trunc);

        if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        return Mono.empty();
    }
}