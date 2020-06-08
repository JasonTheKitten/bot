package everyos.discord.luwu.command.fun;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.MemberAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.LevelCommandHelp, ehelp = LocalizedString.LevelCommandExtendedHelp, category=CategoryEnum.Fun)
public class LevelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		//TODO: Subcommands
		
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			long uid; long iuid;
        	iuid = message.getAuthor().get().getId().asLong();
        	
        	ArgumentParser parser = new ArgumentParser(argument);
        	
        	
        	if (parser.couldBeUserID()) {
        		uid = parser.eatUserID();
        	} else if (parser.isEmpty()) {
        		uid = iuid;
        	} else {
        		return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
        	}
        	
            MemberAdapter madapter = MemberAdapter.of(GuildAdapter.of(data.bot, channel), uid); //TODO: Check uid exists
            
            long fuid = iuid;
            return madapter.getMember().flatMap(member->{
            	return madapter.getDocument().flatMap(doc->{
            		long xpa = doc.getObject().getOrDefaultLong("xp", 0);
                    long xp = xpa;
                    long xpl = xp;
                    int level = 1;
                    int tnl = 3;
                    while (xpl>=tnl) {
                    	level+=1;
                    	xpl-=tnl;
                    	tnl*=1.5;
                    }
                    
                    long xplf = xpl; int levelf = level; int tnlf = tnl;
                    return channel.createEmbed(embed->{
                    	embed.setTitle("User Level"); //TODO: Localize
                    	embed.addField("User", data.safe(member.getUsername()+"#"+member.getDiscriminator()), false);
                    	embed.addField("Level", "Lvl "+String.valueOf(levelf), true);
                    	embed.addField("XP", xp+" xp ("+xplf+"/"+tnlf+")", true);
                    });
            	});
            });
		});
	}
}
