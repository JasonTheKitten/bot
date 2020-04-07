package everyos.discord.bot.command.fun;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.OneWordCommandHelp, ehelp = LocalizedString.OneWordCommandExtendedHelp, category=CategoryEnum.Fun)
public class OneWordCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->member.getBasePermissions())
                .flatMap(perms->{
                    if (!(perms.contains(Permission.ADMINISTRATOR)||perms.contains(Permission.MANAGE_CHANNELS)))
                        return channel.createMessage(data.locale.localize(LocalizedString.InsufficientPermissions));


                    return message.getGuild()
                    	.flatMap(g->g.createTextChannel(c->{
                    		c
                    			.setName("one-word-sentence")
                    			.setRateLimitPerUser(3)
                    			.setTopic(data.localize(LocalizedString.Undocumented));
                    	}))
                    	.flatMap(c->{
	                        ChannelAdapter.of(data.shard, c.getId().asString()).getData((obj, doc)->{
	                            obj.set("type", "oneword");
	                            obj.createObject("data", obj2->obj2.set("sentence", ""));
	                            doc.save();
	                        });
	                        return channel.createMessage(data.locale.localize(LocalizedString.ChannelsSet)).then(Mono.empty());
                    	});
                });
        });
    }
}