package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.channel.ChatLinkCommand;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class ChatLinkChannelCase implements ICommand {
	private HashMap<String, ICommand> commands;
    public ChatLinkChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("link", new ChatLinkCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent().orElse("");
        String trunc = ArgumentParser.getIfPrefix(content, 
            new String[]{"---", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"});

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        return Mono.create(sink->{
            String fromID = message.getChannelId().asString();
            ChannelAdapter.of(data.shard, fromID).getDocument().getObject(obj->{
                if (obj.has("data")&&obj.getOrDefaultObject("data", null).has("chatlinkid")) {
                    sink.success(obj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
                } else sink.error(new Exception("An exception has occured!"));
            });
        })
        .map(s->ChatLinkAdapter.of(data.shard, (String) s))
        .flatMapMany(cla->{
        	String formatted = 
        		message.getAuthor().map(a->a.getUsername()+"#"+a.getDiscriminator()).orElse("<Unknown>")+": "+
        		message.getContent().orElse("<No Content>");
        	
        	return cla.forward("", formatted);
        })
        .flatMap(f->(Mono<?>) f)
        .last();
    }
}
