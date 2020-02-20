package everyos.discord.bot.commands.fun;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;

public class CurrencyCommand implements ICommand {
    private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public CurrencyCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand balanceCommand = new CurrencyBalanceCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("balance", balanceCommand);
        commands.put("bal", balanceCommand);
        lcommands.put(Localization.en_US, commands);
    }

    @Override public void execute(Message message, MessageAdapter adapter, String argument) {
        if (argument.equals("")) argument = "balance";

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
	    adapter.getChannelAdapter(cadapter -> {
	        adapter.getTextLocale(locale->{
	        	ICommand command = lcommands.get(locale).get(cmd);
	            if (command==null) {
	                adapter.formatTextLocale(LocalizedString.NoSuchSubcommand, str->cadapter.send(str));
	                return;
	            }
	            command.execute(message, adapter, arg);
	        });
	    });
    }

    @Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
        return lcommands.containsKey(locale)?lcommands.get(locale):lcommands.get(Localization.en_US);
    }

    @Override public String getBasicUsage(Localization locale) {
        return null;
    }

    @Override public String getExtendedUsage(Localization locale) {
        return null;
    }

    @Override public CategoryEnum getCategory() {
        return CategoryEnum.Fun;
    }
}

class CurrencyBalanceCommand implements ICommand {
    @Override public void execute(Message message, MessageAdapter adapter, String argument) {
        adapter.getChannelAdapter(cadapter->{
            cadapter.send("Ran the feth subcommand");
        });
    }

    @Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }

    @Override public String getBasicUsage(Localization locale) {
        return "[user] Returns a user's balance";
    }

    @Override public String getExtendedUsage(Localization locale) {
        return "[user] The targeted user. Defaults to invoker.";
    }

    @Override public CategoryEnum getCategory() {
        return CategoryEnum.Fun;
    }
}