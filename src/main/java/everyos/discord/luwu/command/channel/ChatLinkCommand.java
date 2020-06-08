package everyos.discord.luwu.command.channel;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.adapter.ChatLinkAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.annotation.Ignorable;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.luwu.util.FillinUtil;
import everyos.discord.luwu.util.PermissionUtil;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

@Ignorable(id=1)
@Help(category=CategoryEnum.Channel)
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
                	if (argument.equals("")) {}; //TODO: Show help instead
                	
                    String cmd = ArgumentParser.getCommand(argument);
                    String arg = ArgumentParser.getArgument(argument);
                    
                    ICommand command = lcommands.get(data.locale.locale).get(cmd);
                    
                    if (command==null)
                    	return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));
            	    
                    return command.execute(message, data, arg);
                });
        });
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

@Ignorable(id=2)
class ChatLinkCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
                return ChannelAdapter.of(data.bot, channel.getId().asLong()).getDocument().flatMap(doc->{
                	DBObject obj = doc.getObject();
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
                    return doc.save().then(Mono.just(id));
                }).cast(Long.class).flatMap(clID->{
                	long cid = channel.getId().asLong();
                    return ChatLinkAdapter.of(data.bot, clID).getDocument().flatMap(cldoc->{
                    	DBObject clobj = cldoc.getObject();
                        clobj.createArray("admins", arr->arr.add(cid));
                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
                        links.add(cid);
                        return cldoc.save();
                    }).then(channel.createMessage(data.localize(LocalizedString.ChatLinkOOBE,
                    	FillinUtil.of("id", String.valueOf(clID), "ping", String.valueOf(data.bot.clientID))))
                        .flatMap(msg->msg.pin()));
                });
			});
	}
}

@Ignorable(id=3)
class ChatLinkJoinCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (parser.isNumerical()) {
				long clid = parser.eatNumerical();
				return ChatLinkDocumentCreateStandard.exists(data.bot, clid).flatMap(exists->{
					if (!exists) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
					
					return ChannelAdapter.of(data.bot, channel.getId().asLong()).getDocument().flatMap(cdoc->{
						DBObject cobj = cdoc.getObject();
                        cobj.set("type", "chatlink");
                        cobj.createObject("data", obj->{
                            obj.set("chatlinkid", clid);
                            obj.set("verified", false);
                        });
                        return cdoc.save();
                    }).then(channel.createMessage(data.localize(LocalizedString.AcceptChatLinkPrompt, FillinUtil.of("id", channel.getId().asString()))));
				});
			} else {
				return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			}
	    });
	}
}