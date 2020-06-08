package everyos.discord.luwu.channelcase;

import java.util.HashMap;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.command.info.HelpCommand;
import everyos.discord.luwu.command.moderation.AnnounceCommand;
import everyos.discord.luwu.command.moderation.BanCommand;
import everyos.discord.luwu.command.moderation.KickCommand;
import everyos.discord.luwu.command.utility.SuggestCommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class SuggestionsChannelCase implements IGroupCommand {
    private HashMap<String, ICommand> commands;
    public SuggestionsChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("help", new HelpCommand());
        commands.put("suggest", new SuggestCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
        commands.put("announce", new AnnounceCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent();
        String trunc = ArgumentParser.getIfPrefix(content, data.prefixes);

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        long fromID = message.getChannelId().asLong();
        return ChannelAdapter.of(data.bot, fromID).getDocument().flatMap(doc->{
        	DBObject obj = doc.getObject();
            if (obj.has("data")&&obj.getOrDefaultObject("data", null).has("out"))
                return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("out", -1L));
            return Mono.error(new Exception("Data field is missing"));
        })
        .flatMap(s->data.bot.client.getChannelById(Snowflake.of(s)).cast(MessageChannel.class))
        .flatMap(c->message.getAuthorAsMember().flatMap(author->
            SuggestCommand.suggest(author, c, data, argument)
        	.then(c.createMessage(author.getMention()))))
        .flatMap(msg->msg.delete())
        .then(message.delete());
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}