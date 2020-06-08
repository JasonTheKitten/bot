package everyos.discord.luwu.command.configuration;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(category=CategoryEnum.Configuration)
public class PrefixCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public PrefixCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand addCommand = new PrefixAddCommand();
        ICommand removeCommand = new PrefixRemoveCommand();
        ICommand resetCommand = new PrefixResetCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("add", addCommand);
        commands.put("remove", removeCommand);
        commands.put("reset", resetCommand);
        //TODO: List command
        lcommands.put(Localization.en_US, commands);
    }
    
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        if (argument.equals("")) {}; //TODO: Show help instead

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null)
        	return Mono.error(new LocalizedException(LocalizedString.NoSuchSubcommand));
	    
        return message.getAuthorAsMember().flatMap(member->PermissionUtil.check(member,
            new Permission[]{Permission.MANAGE_MESSAGES}))
            .then(command.execute(message, data, arg));
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class PrefixAddCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			if (argument.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				DBArray prefixes = doc.getObject().getOrCreateArray("prefixes", ()->DBArray.from(data.prefixes));
				String formatted = argument.replace("%20", " ").replace("%25", "%").trim();
				if (formatted.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.PrefixEmpty));
				if (formatted.length()>20) return Mono.error(new LocalizedException(LocalizedString.PrefixTooLong));
				if (prefixes.contains(formatted)) return Mono.error(new LocalizedException(LocalizedString.PrefixAlreadyExists));
				prefixes.add(formatted);
				
				return doc.save().then(channel.createMessage(data.localize(LocalizedString.PrefixAdded))); //TODO: Indicated the formatted prefix
			});
		});
	}
}

class PrefixRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			if (argument.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				DBArray prefixes = doc.getObject().getOrCreateArray("prefixes", ()->DBArray.from(data.prefixes));
				String formatted = argument.replace("%20", " ").replace("%25", "%");
				if (!prefixes.contains(formatted)) return Mono.error(new LocalizedException(LocalizedString.NoPrefixExists));
				if (prefixes.getLength()==1) return Mono.error(new LocalizedException(LocalizedString.OnePrefixMin));
				prefixes.removeFirst(formatted);
				
				return doc.save().then(channel.createMessage(data.localize(LocalizedString.PrefixAdded)));
			});
		});
	}
}

class PrefixResetCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				doc.getObject().remove("prefixes");
				return doc.save().then(channel.createMessage(data.localize(LocalizedString.PrefixesReset)));
			});
		});
	}
}