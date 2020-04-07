package everyos.discord.bot.command.moderation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.GuildMessageChannel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.PurgeCommandHelp, ehelp = LocalizedString.PurgeCommandExtendedHelp, category=CategoryEnum.Moderation)
public class PurgeCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			//TODO: Check perms
			ArgumentParser parser = new ArgumentParser(argument);
            String after = null; int messages = -1; boolean force;
            if (parser.isNumerical()) {
                messages = (int) parser.eatNumerical();
            } else if (parser.isEmpty()) {
            	return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
            } else {
                switch (parser.eat()) {
                    case "all":
                        break;
                    case "after":
                        if (parser.isNumerical()) {
                            after = parser.eat();
                            break;
                        }
                    default:
                    	return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
                }
            }
            ArrayList<String> channels = new ArrayList<String>();
            HashMap<String, Boolean> users = new HashMap<String, Boolean>();
            while (!parser.isEmpty()) {
                if (parser.isNumerical()) {
                	return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
                } else if (parser.couldBeChannelID()) {
                	String cid = parser.eatChannelID();
                    if (!channels.contains(cid)) channels.add(cid);
                } else if (parser.couldBeUserID()) {
                	String uid = parser.eatUserID();
                    users.put(uid, true);
                } else {
                    String arg = parser.eat();
                    if (arg.equals("--force")||arg.equals("-f")) {
                        force = true;
                    } else {
                    	return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
                    }
                }
            }
            
            if (channels.isEmpty()) channels.add(channel.getId().asString());
            
            Flux<Channel> channelsf;
            channelsf = Flux.just(channels.toArray(new String[channels.size()]))
            	.flatMap(s->data.shard.client.getChannelById(Snowflake.of(s))); //TODO: Guild-get channel instead
            
            String afterf = after; int messagesf = messages;
            
            return channelsf
            	.flatMap(c->{
            		if (!(c instanceof MessageChannel)) return Mono.error(new Exception());
            		GuildMessageChannel mc = (GuildMessageChannel) c;
            		
            		AtomicInteger mcount = new AtomicInteger();
            		Vector<Message> messagestd = new Vector<Message>();
            		
            		return mc.bulkDelete(
            			mc.createMessage(data.localize(LocalizedString.AutoDelete))
	            			.doOnNext(m->m.delete().subscribe())
	            			.flatMapMany(m->{
	            				if (afterf==null) {
	            					return mc.getMessagesBefore(m.getId());
	            				} else return mc.getMessagesAfter(Snowflake.of(afterf));
	            			})
	            			.filter(m->{
	            				return (messagesf==-1||mcount.getAndIncrement()<messagesf)&&
	            					(users.isEmpty()||(m.getAuthor().isPresent()&&users.get(m.getAuthor().get().getId().asString())));
	            				//TODO: Force
	            			})
	            			.map(m->m.getId())
            		);
            	})
            	.then(channel.createMessage(data.localize(LocalizedString.MessagesPurged)));
		});
	}
}