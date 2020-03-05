package everyos.discord.bot.commands.fun;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.object.CategoryEnum;

public class MusicCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Fun;
	}
}

class MusicPlayCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicPlayListCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicQueueCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicSkipCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicNowPlayingCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicPauseCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicUnpauseCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicRepeatCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

class MusicStopCommand implements IMusicCommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	
	@Override public CategoryEnum getCategory() { return null; }
}

interface IMusicCommand extends ICommand {
    //public void execute(Message message, MessageAdapter adapter, String argument);
} 