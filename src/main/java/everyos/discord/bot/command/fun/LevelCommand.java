package everyos.discord.bot.command.fun;

import java.util.concurrent.atomic.AtomicInteger;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.TopEntityAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class LevelCommand implements IGroupCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		//TODO: Subcommands
		
		return message.getChannel().flatMap(channel->{
			String uid = null;
        	String iuid = null;
        	iuid = message.getAuthor().get().getId().asString();
        	
        	ArgumentParser parser = new ArgumentParser(argument);
        	
        	
        	if (parser.couldBeUserID()) {
        		uid = parser.eatUserID();
        	} else if (parser.isEmpty()) {
        		uid = iuid;
        	} else {
        		return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
        	}
            TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
            if (!teadapter.isOfGuild()) return Mono.empty(); //TODO
            MemberAdapter madapter = MemberAdapter.of((GuildAdapter) teadapter.getPrimaryAdapter(), uid); //TODO: Check uid exists
            
            String fuid = iuid;
            return madapter.getMember()
            	.flatMap(member->{
            		AtomicInteger xpa = new AtomicInteger();
                    madapter.getDocument().getObject(obj->xpa.set(obj.getOrDefaultInt("xp", 0)));
                    int xp = xpa.get();
                    int xpl = xp;
                    int level = 1;
                    int tnl = 3;
                    while (xpl>=tnl) {
                    	level+=1;
                    	xpl-=tnl;
                    	tnl*=1.5;
                    }
                    
                    int xplf = xpl; int levelf = level; int tnlf = tnl;
                    return channel.createEmbed(embed->{
                    	embed.setTitle("User Level"); //TODO: Localize
                    	embed.addField("User", data.safe(member.getUsername()+"#"+member.getDiscriminator()), false);
                    	embed.addField("Level", "Lvl "+String.valueOf(levelf), true);
                    	embed.addField("XP", xp+" xp ("+xplf+"/"+tnlf+")", true);
                    });
            	});
		});
	}
}
