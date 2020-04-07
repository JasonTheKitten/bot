package everyos.discord.bot.command.fun;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.LevelCommandHelp, ehelp = LocalizedString.LevelCommandExtendedHelp, category=CategoryEnum.Fun)
public class LevelCommand implements ICommand {
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
        	
            MemberAdapter madapter = MemberAdapter.of(GuildAdapter.of(data.shard, channel), uid); //TODO: Check uid exists
            
            String fuid = iuid;
            return madapter.getMember()
            	.flatMap(member->{
            		int xpa = madapter.getData(obj->obj.getOrDefaultInt("xp", 0));
                    int xp = xpa;
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
