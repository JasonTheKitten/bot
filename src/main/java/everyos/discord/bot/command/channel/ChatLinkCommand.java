package everyos.discord.bot.command.channel;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.annotation.Ignorable;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.InvalidSubcommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.PermissionUtil;
import everyos.storage.database.DBArray;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

@Ignorable(id=1)
public class ChatLinkCommand implements IGroupCommand {
    public HashMap<Localization, HashMap<String, ICommand>> lcommands;
    public ChatLinkCommand() {
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();
        HashMap<String, ICommand> commands;

        //Commands
        ICommand createCommand = new ChatLinkCreateCommand();
        ICommand joinCommand = new ChatLinkJoinCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("create", createCommand);
        commands.put("join", joinCommand);
        lcommands.put(Localization.en_US, commands);
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->PermissionUtil.check(member, new Permission[] {Permission.MANAGE_CHANNELS}))
                .flatMap(member->{
                    String cmd = ArgumentParser.getCommand(argument);
					String arg = ArgumentParser.getArgument(argument);

                    return lcommands.get(data.locale.locale).getOrDefault(cmd, new InvalidSubcommand()).execute(message, data, arg);
                })
                .cast(Object.class);
        });
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

@Ignorable(id=2)
class ChatLinkCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
                return ChannelAdapter.of(data.shard, channel.getId().asLong()).getDocument().getObject((obj, doc)->{
                    if (obj.getOrDefaultString("type", "default")!="default") {
                        //TODO: Warning+Confirmation here?
                        return Mono.error(new LocalizedException(LocalizedString.ChannelAlreadyInUse));
                    }

                    Snowflake factory = new Snowflake(0, 0);
                    long id = factory.nextId();
                    
                    obj.set("type", "chatlink");
                    obj.createObject("data", cdobj->{
                        cdobj.set("verified", true);
                        cdobj.set("chatlinkid", id);
                    });
                    doc.save();

                    return Mono.just(id);
                }).cast(Long.class).flatMap(clID->{
                	long cid = channel.getId().asLong();
                    ChatLinkAdapter.of(data.shard, clID).getData((clobj, cldoc)->{
                        clobj.createArray("admins", arr->{
                            arr.add(cid);
                        });
                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
                        links.add(cid);
                        cldoc.save();
                    });
                    return channel.createMessage(data.locale.localize(LocalizedString.ChatLinkOOBE,
                    	FillinUtil.of("id", String.valueOf(clID), "ping", String.valueOf(data.shard.clientID))))
                        .flatMap(msg->msg.pin());
                });
			});
	}
}

@Ignorable(id=3)
class ChatLinkJoinCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.isNumerical()) {
					long clid = parser.eatNumerical();
					if (ChatLinkDocumentCreateStandard.exists(data.bot, clid)) {
						ChannelAdapter.of(data.shard, channel.getId().asLong()).getDocument().getObject((cobj, cdoc)->{
                            cobj.set("type", "chatlink");
                            cobj.createObject("data", obj->{
                                obj.set("chatlinkid", clid);
                            });
                            cdoc.save();
                        });
                        
                        return channel.createMessage(data.locale.localize(LocalizedString.AcceptChatLinkPrompt, FillinUtil.of("id", channel.getId().asString())));
					} else {
						return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
                    }
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
		    });
	}
}