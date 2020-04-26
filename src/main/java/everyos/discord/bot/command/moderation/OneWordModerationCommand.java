package everyos.discord.bot.command.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.OWMCommandHelp, ehelp = LocalizedString.OWMCommandExtendedHelp, category=CategoryEnum.Moderation)
public class OneWordModerationCommand implements IGroupCommand {	
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public OneWordModerationCommand() {
		HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand resetCommand = new OneWordResetCommand();
        ICommand lastUserCommand = new OneWordLastUserCommand();
        ICommand removePhraseCommand = new OneWordRemovePhraseCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("reset", resetCommand);
        commands.put("lastuser", lastUserCommand);
        commands.put("remove", removePhraseCommand);
        lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		if (argument.equals("")) return Mono.empty(); //TODO: Help

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null)
        	return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));
	    
        return message.getAuthorAsMember().flatMap(member->PermissionUtil.check(member,
        	new Permission[]{Permission.MANAGE_CHANNELS},
        	new Permission[]{Permission.MANAGE_MESSAGES}))
        	.then(command.execute(message, data, arg));
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class OneWordResetCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return ChannelAdapter.of(data.bot, message.getChannelId().asLong()).getDocument().flatMap(doc->{
				doc.getObject().getOrCreateObject("data", ()->new DBObject()).set("sentence", "");
				doc.getObject().getOrCreateObject("data", ()->new DBObject()).set("lastuser", -1L);
				return doc.save().then(channel.createMessage(data.localize(LocalizedString.OneWordSentenceReset)));
			});
		});
	}
}
class OneWordLastUserCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return ChannelAdapter.of(data.bot, channel.getId().asLong()).getDocument().flatMap(doc->{
				long uid = doc.getObject().getOrCreateObject("data", ()->new DBObject()).getOrDefaultLong("lastuser", -1L);
				return channel.createMessage(data.localize(LocalizedString.OneWordLastUser, FillinUtil.of("ping", "<@"+uid+">")));
			});
		});
	}
}
class OneWordRemovePhraseCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return ChannelAdapter.of(data.bot, message.getChannelId().asLong()).getDocument().flatMap(doc->{
				DBObject dobj = doc.getObject().getOrCreateObject("data", ()->new DBObject());
				String sentence = dobj.getOrDefaultString("sentence", "").replace(argument.trim(), "");
				dobj.set("sentence", sentence);
				return channel.createMessage(sentence);
			});
		});
	}
}