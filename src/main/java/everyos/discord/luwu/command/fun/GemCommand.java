package everyos.discord.luwu.command.fun;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.MemberAdapter;
import everyos.discord.luwu.adapter.UserAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.FillinUtil;
import reactor.core.publisher.Mono;

//TODO: Localize more
@Help(help=LocalizedString.CurrencyCommandHelp, ehelp = LocalizedString.CurrencyCommandExtendedHelp, category=CategoryEnum.Fun)
public class GemCommand implements IGroupCommand {
    private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public GemCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand balanceCommand = new GemBalanceCommand();
        ICommand giveCommand = new GemGiveCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("balance", balanceCommand);
        commands.put("bal", balanceCommand);
        commands.put("give", giveCommand);
        lcommands.put(Localization.en_US, commands);
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        if (argument.equals("")) argument = "balance"; //TODO: Show help instead

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null)
        	return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));
	    
        return command.execute(message, data, arg);
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class GemBalanceCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
        	long uid;
        	long iuid;
        	iuid = message.getAuthor().get().getId().asLong();
        	
        	ArgumentParser parser = new ArgumentParser(argument);
        	
        	
        	if (parser.couldBeUserID()) {
        		uid = parser.eatUserID();
        	} else if (parser.isEmpty()) {
        		uid = iuid;
        	} else {
        		return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
        	}
            UserAdapter madapter = UserAdapter.of(data.bot, uid); //TODO: Check uid exists
            MemberAdapter m2adapter = MemberAdapter.of(GuildAdapter.of(data.bot, channel), uid);
            
            long fuid = iuid;
            return m2adapter.getMember().flatMap(member->{
            	return madapter.getDocument().flatMap(doc->{
            		String targetnameraw = member.getDisplayName();
            		String targetname = targetnameraw+"'s";
            		if (member.getId().asLong()==fuid) targetname = "Your";
            		
            		long gems = doc.getObject().getOrDefaultLong("gems", 0);
                    if (gems == 0) {
                        return channel.createMessage(data.localize(LocalizedString.ZeroBalance,
                        	FillinUtil.of("gems", String.valueOf(gems), "target", targetname))); 
                        //I don't think this is actually reachable under normal use
                    } else {
                    	return channel.createMessage(data.localize(LocalizedString.CurrentBalance, 
                        	FillinUtil.of("gems", String.valueOf(gems), "target", targetname)));
                    }
            	});
        	});
        });
    }
}

class GemGiveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().cast(GuildMessageChannel.class)
        	.flatMap(channel->{
        		long uid = message.getAuthor().get().getId().asLong();
	        	ArgumentParser parser = new ArgumentParser(argument);
	        	if (parser.couldBeUserID()) {
	        		UserAdapter madapter = UserAdapter.of(data.bot, parser.eatUserID());
                    //Should I continue to allow sending money to users who have left?
        			
                    if (parser.isNumerical()) {
                    	if (madapter.getUserID()==uid)
                    		return Mono.error(new LocalizedException(LocalizedString.SelfSendMoney));
                    	
                    	if (madapter.getUserID()==data.bot.clientID)
                    		return channel.createMessage(data.localize(LocalizedString.SendMoneyThankYou));
                    		
                        int amount = (int) parser.eatNumerical();
                        UserAdapter invoker = UserAdapter.of(data.bot, uid);
                        
                        return invoker.getDocument().flatMap(pdoc->{
                        	DBObject pobj = pdoc.getObject();
                        	int igems = pobj.getOrDefaultInt("gems", 0);
                            if (amount<0)
                            	return Mono.error(new LocalizedException(LocalizedString.CurrencyStealing));
                            if (igems<amount)
                            	return Mono.error(new LocalizedException(LocalizedString.NotEnoughCurrency));
                            
                            return madapter.getDocument().flatMap(tdoc->{
                            	DBObject tobj = tdoc.getObject();
	                            pobj.set("gems", igems-amount);
	                            tobj.set("gems", tobj.getOrDefaultInt("gems", 0)+amount);
	                            return pdoc.save().and(tdoc.save()).then(channel.createMessage(data.localize(LocalizedString.MoneySent)));
                            });
                        });
                    } else {
                        return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
                    }
        		} else {
        			return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
        		}
        });
    }
}