package everyos.discord.bot.commands.info;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.Main;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.TimeUtil;

public class UptimeCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		long m = System.currentTimeMillis();
		long uptime = m - Main.uptime;
		long cuptime = m - Main.cuptime.get();
		
		String hours = String.valueOf(TimeUtil.getHours(uptime, false));
    	String minutes = String.valueOf(TimeUtil.getMinutes(uptime, true));
    	String seconds = String.valueOf(TimeUtil.getSeconds(uptime, true));
    	
    	String chours = String.valueOf(TimeUtil.getHours(cuptime, false));
    	String cminutes = String.valueOf(TimeUtil.getMinutes(cuptime, true));
    	String cseconds = String.valueOf(TimeUtil.getSeconds(cuptime, true));
    	
    	adapter.getChannelAdapter(cadapter->
    		adapter.formatTextLocale(LocalizedString.Uptime, 
    			FillinUtil.of("h", hours, "m", minutes, "s", seconds, "ch", chours, "cm", cminutes, "cs", cseconds), 
    			str->cadapter.send(str))
    	);
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
		return null;
	}
	@Override public String getBasicUsage(Localization locale) {
		return null;
	}
	@Override public String getExtendedUsage(Localization locale) {
		return null;
	}
	@Override public CategoryEnum getCategory() {
		return null;
	}
}
