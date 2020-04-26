package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandAlias;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.channel.ChatLinkCommand;
import everyos.discord.bot.command.channel.OneWordCommand;
import everyos.discord.bot.command.channel.StarboardCommand;
import everyos.discord.bot.command.channel.SuggestionsCommand;
import everyos.discord.bot.command.configuration.PrefixCommand;
import everyos.discord.bot.command.fun.CurrencyCommand;
import everyos.discord.bot.command.fun.GiphyCommand;
import everyos.discord.bot.command.fun.HugCommand;
import everyos.discord.bot.command.fun.LevelCommand;
import everyos.discord.bot.command.fun.MusicCommand;
import everyos.discord.bot.command.info.DBLVoteCommand;
import everyos.discord.bot.command.info.DonateCommand;
import everyos.discord.bot.command.info.HelpCommand;
import everyos.discord.bot.command.info.InfoCommand;
import everyos.discord.bot.command.info.PingCommand;
import everyos.discord.bot.command.info.ProfileCommand;
import everyos.discord.bot.command.info.SupportCommand;
import everyos.discord.bot.command.info.UptimeCommand;
import everyos.discord.bot.command.moderation.BanCommand;
import everyos.discord.bot.command.moderation.IgnoreCommand;
import everyos.discord.bot.command.moderation.KickCommand;
import everyos.discord.bot.command.moderation.MuteCommand;
import everyos.discord.bot.command.moderation.PurgeCommand;
import everyos.discord.bot.command.moderation.UnmuteCommand;
import everyos.discord.bot.command.utility.AutoRoleCommand;
import everyos.discord.bot.command.utility.DictionaryCommand;
import everyos.discord.bot.command.utility.GiveawayCommand;
import everyos.discord.bot.command.utility.LeaveCommand;
import everyos.discord.bot.command.utility.ReactionCommand;
import everyos.discord.bot.command.utility.RoleCommand;
import everyos.discord.bot.command.utility.SuggestCommand;
import everyos.discord.bot.command.utility.TicketCommand;
import everyos.discord.bot.command.utility.TranslateCommand;
import everyos.discord.bot.command.utility.WelcomeCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class DefaultChannelCase implements IGroupCommand {
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
        commands.put("oneword", new OneWordCommand());
        commands.put("hug", new HugCommand());
        commands.put("giphy", new GiphyCommand());
        commands.put("level", new LevelCommand());
        commands.put("music", new MusicCommand());
        commands.put("purge", new PurgeCommand());
        commands.put("info", new InfoCommand());
        commands.put("giveaway", new GiveawayCommand());//
        commands.put("ticket", new TicketCommand());
        commands.put("role", new RoleCommand());
        commands.put("welcome", new WelcomeCommand());
        commands.put("leave", new LeaveCommand());
        commands.put("donate", new DonateCommand());
        commands.put("support", new SupportCommand());
        commands.put("autorole", new AutoRoleCommand());
        commands.put("vote", new DBLVoteCommand());
        commands.put("reaction", new ReactionCommand());
        commands.put("starboard", new StarboardCommand());
        commands.put("dict", new DictionaryCommand());
        commands.put("help", new HelpCommand());
        commands.put("translate", new TranslateCommand());
        commands.put("ignore", new IgnoreCommand());
        commands.put("mute", new MuteCommand());
        commands.put("unmute", new UnmuteCommand());
        commands.put("prefix", new PrefixCommand());
        
        commands.put("m", new CommandAlias(commands.get("music"), "music"));
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