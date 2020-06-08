package everyos.discord.luwu.command.channel;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.annotation.Ignorable;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.BotPermissionUtil;
import reactor.core.publisher.Mono;

@Ignorable(id=5)
@Help(help=LocalizedString.OneWordCommandHelp, ehelp = LocalizedString.OneWordCommandExtendedHelp, category=CategoryEnum.Channel)
public class OneWordCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
        	.flatMap(channel->BotPermissionUtil.check(channel, new Permission[] {Permission.MANAGE_CHANNELS}).then(Mono.just(channel)))
        	.flatMap(channel->{
	            return message.getAuthorAsMember()
	                .flatMap(member->member.getBasePermissions())
	                .flatMap(perms->{
	                    if (!(perms.contains(Permission.ADMINISTRATOR)||perms.contains(Permission.MANAGE_CHANNELS)))
	                        return channel.createMessage(data.localize(LocalizedString.InsufficientPermissions));
	
	
	                    return message.getGuild()
	                    	.flatMap(g->g.createTextChannel(c->{
	                    		c
	                    			.setName("one-word-sentence")
	                    			.setRateLimitPerUser(3)
	                    			.setTopic(data.localize(LocalizedString.Undocumented));
	                    	}))
	                    	.flatMap(c->{
		                        return ChannelAdapter.of(data.bot, c.getId().asLong()).getDocument().flatMap(doc->{
		                        	DBObject obj = doc.getObject();
		                            obj.set("type", "oneword");
		                            obj.createObject("data", obj2->obj2.set("sentence", ""));
		                            return doc.save();
		                        }).then(channel.createMessage(data.localize(LocalizedString.ChannelsSet)));
	                    	});
	                });
	        });
    }
}