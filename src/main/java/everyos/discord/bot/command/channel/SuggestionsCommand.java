package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class SuggestionsCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->member.getBasePermissions())
                .flatMap(perms->{
                    if (!(perms.contains(Permission.ADMINISTRATOR)||perms.contains(Permission.MANAGE_CHANNELS)))
                        return channel.createMessage(data.locale.localize(LocalizedString.InsufficientPermissions));

                    String fromID; String toID;
                    ArgumentParser parser = new ArgumentParser(argument);
                    if (parser.isEmpty()) {
                        fromID = channel.getId().asString();
                        toID = channel.getId().asString();
                    } else if (parser.couldBeChannelID()) {
                        fromID = parser.eatChannelID();
                        if (parser.isEmpty()) {
                            toID = fromID;
                        } else if (parser.couldBeChannelID()) {
                            toID = parser.eatChannelID();
                        } else {
                            return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
                        }
                    } else {
                        return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
                    }

                    return message.getGuild().flatMap(guild->{
                        return guild
                            .getChannelById(Snowflake.of(fromID))
                            .flatMap(c->guild.getChannelById(Snowflake.of(toID)))
                            .onErrorResume(e->channel.createMessage(
                                data.locale.localize(LocalizedString.UnrecognizedChannel)).flatMap(m->Mono.empty()));
                    }).flatMap(c->{
                        ChannelAdapter.of(data.shard, fromID).getDocument().getObject((obj, doc)->{
                            obj.set("type", "suggestions");
                            obj.createObject("data", obj2->obj2.set("out", toID));
                            doc.save();
                        });
                        return channel.createMessage(data.locale.localize(LocalizedString.ChannelsSet)).flatMap(m->Mono.empty());
                    });
                });
        });
    }
}