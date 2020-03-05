package everyos.discord.bot.commands.info;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.StringUtil;

public class HelpCommand implements ICommand {
    private static HelpCommand defHelpCommand;
    static {
        defHelpCommand = new HelpCommand();
        defHelpCommand.def = true;
    }

    @SuppressWarnings("unused")
    private boolean def = false;

    @Override public void execute(Message message, MessageAdapter adapter, final String argument) {
        adapter.getChannelAdapter(cadapter -> {
	        adapter.getTextLocale(locale->{
	        	ICommand command = defHelpCommand;
	        	String name = "help"; //TODO: Should be localized
	            String group = null;
	            String parg = argument;
		        while (parg.length()>0) {
		            name = ArgumentParser.getCommand(parg);
		            parg = ArgumentParser.getCommand(parg);
		            command = command.getSubcommands(locale).get(name);
		
		            if (command == null) {
		                adapter.formatTextLocale(LocalizedString.NoSuchCommand, str->cadapter.send(str));
		                return;
		            }
		
		            if (parg.startsWith(">")) {
		                group = StringUtil.sub(parg, 1, 2).toUpperCase()+StringUtil.sub(parg, 2);
		                break;
		            }
		        }
		
		        HashMap<String, ICommand> commands = command.getSubcommands(locale);
		        if (group!=null) {
		            if (commands==null) {
		                adapter.formatTextLocale(LocalizedString.NoSuchGroup, str->cadapter.send(str));
		                return;
		            }
		        } else if (commands == null) {
		            cadapter.sendEmbed(embed->{
		                embed.setTitle(adapter.formatTextLocale(locale, LocalizedString.ExtendedHelp, FillinUtil.of("command", argument)));
		            });
		        } else {
		            cadapter.sendEmbed(embed->{
		                embed.setTitle(adapter.formatTextLocale(locale, LocalizedString.CommandHelp, FillinUtil.of("command", argument)));
		            });
		        }
	        });
        });
    }

    @Override public String getBasicUsage(Localization locale) {
        return "[command][subcommands+] Displays help for commands";
    }

    @Override public String getExtendedUsage(Localization locale) {
        return null;
    }

    @Override public CategoryEnum getCategory() {
        return CategoryEnum.Info;
    }

    @Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
        return null;
    }
}