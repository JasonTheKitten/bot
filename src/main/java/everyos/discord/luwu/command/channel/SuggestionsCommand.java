package everyos.discord.luwu.command.channel;

import discord4j.common.util.Snowflake;
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
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Ignorable(id=8)
@Help(help=LocalizedString.SuggestionsCommandHelp, ehelp=LocalizedString.SuggestionsCommandExtendedHelp, category=CategoryEnum.Channel)
public class SuggestionsCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->PermissionUtil.check(member, Permission.MANAGE_CHANNELS))
                .flatMap(member->{
                    long fromID; long toID;
                    ArgumentParser parser = new ArgumentParser(argument);
                    if (parser.isEmpty()) {
                        fromID = channel.getId().asLong();
                        toID = channel.getId().asLong();
                    } else if (parser.couldBeChannelID()) {
                        fromID = parser.eatChannelID();
                        if (parser.isEmpty()) {
                            toID = fromID;
                        } else if (parser.couldBeChannelID()) {
                            toID = parser.eatChannelID();
                        } else {
                            return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
                        }
                    } else {
                    	return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
                    }

                    return message.getGuild().flatMap(guild->{
                        return guild
                            .getChannelById(Snowflake.of(fromID))
                            .flatMap(c->guild.getChannelById(Snowflake.of(toID)))
                            .onErrorResume(e->Mono.error(new LocalizedException(LocalizedString.UnrecognizedChannel)));
                    }).flatMap(c->{
                        return ChannelAdapter.of(data.bot, fromID).getDocument().flatMap(doc->{
                        	DBObject obj = doc.getObject();
                            obj.set("type", "suggestions");
                            obj.createObject("data", obj2->obj2.set("out", toID));
                            return doc.save().then(channel.createMessage(data.localize(LocalizedString.ChannelsSet)));
                        });
                    });
                });
        });
    }
}