package everyos.discord.luwu.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandAlias;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.command.channel.ChatLinkCommand;
import everyos.discord.luwu.command.channel.OneWordCommand;
import everyos.discord.luwu.command.channel.StarboardCommand;
import everyos.discord.luwu.command.channel.SuggestionsCommand;
import everyos.discord.luwu.command.configuration.PrefixCommand;
import everyos.discord.luwu.command.fun.BattleCommand;
import everyos.discord.luwu.command.fun.FethCommand;
import everyos.discord.luwu.command.fun.GiphyCommand;
import everyos.discord.luwu.command.fun.HugCommand;
import everyos.discord.luwu.command.fun.LevelCommand;
import everyos.discord.luwu.command.fun.MusicCommand;
import everyos.discord.luwu.command.info.DBLVoteCommand;
import everyos.discord.luwu.command.info.DonateCommand;
import everyos.discord.luwu.command.info.HelpCommand;
import everyos.discord.luwu.command.info.InfoCommand;
import everyos.discord.luwu.command.info.PingCommand;
import everyos.discord.luwu.command.info.ProfileCommand;
import everyos.discord.luwu.command.info.SupportCommand;
import everyos.discord.luwu.command.info.UptimeCommand;
import everyos.discord.luwu.command.moderation.BanCommand;
import everyos.discord.luwu.command.moderation.IgnoreCommand;
import everyos.discord.luwu.command.moderation.KickCommand;
import everyos.discord.luwu.command.moderation.LoggingChannelCommand;
import everyos.discord.luwu.command.moderation.MuteCommand;
import everyos.discord.luwu.command.moderation.PurgeCommand;
import everyos.discord.luwu.command.moderation.UnmuteCommand;
import everyos.discord.luwu.command.privacy.GuildDataClearCommand;
import everyos.discord.luwu.command.utility.AutoRoleCommand;
import everyos.discord.luwu.command.utility.DictionaryCommand;
import everyos.discord.luwu.command.utility.EmbedCommand;
import everyos.discord.luwu.command.utility.GhostCommand;
import everyos.discord.luwu.command.utility.LeaveCommand;
import everyos.discord.luwu.command.utility.ReactionCommand;
import everyos.discord.luwu.command.utility.RoleCommand;
import everyos.discord.luwu.command.utility.SuggestCommand;
import everyos.discord.luwu.command.utility.TicketCommand;
import everyos.discord.luwu.command.utility.TranslateCommand;
import everyos.discord.luwu.command.utility.WelcomeCommand;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.parser.ArgumentParser;
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
        commands.put("feth", new FethCommand());
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
        //commands.put("giveaway", new GiveawayCommand());//
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
        //commands.put("modlogs", new ModLogsCommand());
        commands.put("ghost", new GhostCommand());
        commands.put("logs", new LoggingChannelCommand());
        commands.put("guildreset", new GuildDataClearCommand());
        commands.put("embed", new EmbedCommand());
        //commands.put("battle", new BattleCommand());
        
        commands.put("m", new CommandAlias(commands.get("music"), "music"));
        commands.put("invite", commands.get("info"));
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