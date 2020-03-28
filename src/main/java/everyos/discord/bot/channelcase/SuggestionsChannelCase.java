package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.moderation.BanCommand;
import everyos.discord.bot.command.moderation.KickCommand;
import everyos.discord.bot.command.utility.SuggestCommand;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class SuggestionsChannelCase implements IGroupCommand {
    private HashMap<String, ICommand> commands;
    public SuggestionsChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("suggest", new SuggestCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent().orElse("");
        String trunc = ArgumentParser.getIfPrefix(content, 
            new String[] {"---", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"});

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        return Mono.create(sink->{
            String fromID = message.getChannelId().asString();
            ChannelAdapter.of(data.shard, fromID).getDocument().getObject(obj->{
                if (obj.has("data")&&obj.getOrDefaultObject("data", null).has("out")) {
                    sink.success(obj.getOrDefaultObject("data", null).getOrDefaultString("out", null));
                } else sink.error(new Exception("An exception has occured!"));
            });
        })
        .flatMap(s->data.shard.client.getChannelById(Snowflake.of((String) s)))
        .flatMap(c->message.getAuthorAsMember().flatMap(author->
            SuggestCommand.suggest(author, (MessageChannel) c, data, argument)))
        .flatMap(o->message.delete());
    }
}