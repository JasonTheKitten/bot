package everyos.discord.luwu.command.moderation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.BotPermissionUtil;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.PurgeCommandHelp, ehelp = LocalizedString.PurgeCommandExtendedHelp, category=CategoryEnum.Moderation)
public class PurgeCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember().flatMap(member->PermissionUtil.check(member,
			new Permission[]{Permission.MANAGE_MESSAGES},
			new Permission[]{Permission.MANAGE_CHANNELS}))
			.then(message.getChannel())
			.flatMap(channel->BotPermissionUtil.check(channel, new Permission[] {Permission.MANAGE_MESSAGES}).then(Mono.just(channel)))
			.flatMap(channel->{
				
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
            ArrayList<Long> channels = new ArrayList<Long>();
            HashMap<Long, Boolean> users = new HashMap<Long, Boolean>();
            while (!parser.isEmpty()) {
                if (parser.isNumerical()) {
                	return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
                } else if (parser.couldBeChannelID()) {
                	long cid = parser.eatChannelID();
                    if (!channels.contains(cid)) channels.add(cid);
                } else if (parser.couldBeUserID()) {
                	long uid = parser.eatUserID();
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
            
            if (channels.isEmpty()) channels.add(channel.getId().asLong());
            
            Flux<Channel> channelsf;
            channelsf = Flux.just(channels.toArray(new Long[channels.size()]))
            	.flatMap(s->data.bot.client.getChannelById(Snowflake.of(s))); //TODO: Guild-get channel instead
            
            String afterf = after; int messagesf = messages;
            
            return channelsf.flatMap(c->{
        		if (!(c instanceof MessageChannel)) return Mono.error(new Exception());
        		GuildMessageChannel mc = (GuildMessageChannel) c;
        		
        		AtomicInteger mcount = new AtomicInteger();
        		Vector<Message> messagestd = new Vector<Message>();
        		
        		return mc.bulkDelete(
        			mc.createMessage(data.localize(LocalizedString.AutoDelete))
            			.flatMap(m->m.delete().then(Mono.just(m)))
            			.flatMapMany(m->{
            				if (afterf==null) {
            					return mc.getMessagesBefore(m.getId());
            				} else return mc.getMessagesAfter(Snowflake.of(afterf));
            			})
            			.filter(m->{
            				return (messagesf==-1||mcount.getAndIncrement()<messagesf)&&
            					(users.isEmpty()||(m.getAuthor().isPresent()&&users.getOrDefault(m.getAuthor().get().getId().asLong(), false)));
            				//TODO: Force
            			})
            			.map(m->m.getId())
        		);
        	}).then(channel.createMessage(data.localize(LocalizedString.MessagesPurged)));
		});
	}
}