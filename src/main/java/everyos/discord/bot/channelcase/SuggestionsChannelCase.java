package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.info.HelpCommand;
import everyos.discord.bot.command.moderation.BanCommand;
import everyos.discord.bot.command.moderation.KickCommand;
import everyos.discord.bot.command.utility.SuggestCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class SuggestionsChannelCase implements IGroupCommand {
    private HashMap<String, ICommand> commands;
    public SuggestionsChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("help", new HelpCommand());
        commands.put("suggest", new SuggestCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent();
        String trunc = ArgumentParser.getIfPrefix(content, 
            new String[] {"---", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"});

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        long fromID = message.getChannelId().asLong();
        return ChannelAdapter.of(data.shard, fromID).getData(obj->{
            if (obj.has("data")&&obj.getOrDefaultObject("data", null).has("out"))
                return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultString("out", null));
            return Mono.error(new Exception("Data field is missing"));
        })
        .flatMap(s->data.shard.client.getChannelById(Snowflake.of((String) s))).cast(MessageChannel.class)
        .flatMap(c->message.getAuthorAsMember().flatMap(author->
            SuggestCommand.suggest(author, c, data, argument)
        	.then(c.createMessage(author.getMention()))))
        .flatMap(msg->msg.delete())
        .then(message.delete());
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}