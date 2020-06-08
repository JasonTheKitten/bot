package everyos.discord.luwu.command.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.mongodb.client.model.Filters;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.MemberAdapter;
import everyos.discord.luwu.adapter.TopEntityAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBDocument;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.FillinUtil;
import everyos.discord.luwu.util.TimeUtil;
import reactor.core.publisher.Mono;

//TODO: Localize more
@Help(help=LocalizedString.CurrencyCommandHelp, ehelp = LocalizedString.CurrencyCommandExtendedHelp, category=CategoryEnum.Fun)
public class FethCommand implements IGroupCommand {
    private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public FethCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand balanceCommand = new GemBalanceCommand();
        ICommand dailyCommand = new CurrencyDailyCommand();
        ICommand giveCommand = new GemGiveCommand();
        ICommand leaderboardCommand = new CurrencyTopCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("balance", balanceCommand);
        commands.put("bal", balanceCommand);
        commands.put("daily", dailyCommand);
        commands.put("give", giveCommand);
        commands.put("top", leaderboardCommand);
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

@Help(help=LocalizedString.CurrencyBalanceCommandHelp, ehelp=LocalizedString.CurrencyBalanceCommandExtendedHelp)
class CurrencyBalanceCommand implements ICommand {
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
            MemberAdapter madapter = MemberAdapter.of(GuildAdapter.of(data.bot, channel), uid); //TODO: Check uid exists
            
            long fuid = iuid;
            return madapter.getMember().flatMap(member->{
            	return madapter.getDocument().flatMap(doc->{
            		String targetnameraw = member.getDisplayName();
            		String targetname = targetnameraw+"'s";
            		if (member.getId().asLong()==fuid) targetname = "Your";
            		
            		long feth = doc.getObject().getOrDefaultLong("feth", 0);
                    if (feth == 0) {
                        return channel.createMessage(data.localize(LocalizedString.ZeroBalance,
                        	FillinUtil.of("feth", String.valueOf(feth), "target", targetname))); 
                        //I don't think this is actually reachable under normal use
                    } else {
                    	return channel.createMessage(data.localize(LocalizedString.CurrentBalance, 
                        	FillinUtil.of("feth", String.valueOf(feth), "target", targetname)));
                    }
            	});
        	});
        });
    }
}

@Help(help=LocalizedString.CurrencyDailyCommandHelp, ehelp=LocalizedString.CurrencyDailyCommandExtendedHelp)
class CurrencyDailyCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
        	TopEntityAdapter teadapter = TopEntityAdapter.of(data.bot, channel);
        	if (!teadapter.isOfGuild()) return Mono.empty();
        	GuildAdapter gadapter = (GuildAdapter) teadapter.getPrimaryAdapter();
            return MemberAdapter.of(gadapter, message.getAuthor().orElse(null).getId().asLong()).getDocument().flatMap(doc->{
            	DBObject obj = doc.getObject();
            	long curtime = System.currentTimeMillis();
                long time = obj.getOrDefaultLong("fethdaily", 0);
                long timeleft = time-curtime;
                if (timeleft<=0) {
                	obj.set("fethdaily", System.currentTimeMillis()+(24*60*60*1000));
                    obj.set("feth",  obj.getOrDefaultLong("feth", 0)+20);
                    return doc.save().then(channel.createMessage(data.localize(LocalizedString.ReceivedDaily, FillinUtil.of("feth", "20"))));
                } else {
                	String hours = String.valueOf(TimeUtil.getHours(timeleft, false));
                	String minutes = String.valueOf(TimeUtil.getMinutes(timeleft, true));
                	String seconds = String.valueOf(TimeUtil.getSeconds(timeleft, true));
                	return channel.createMessage(data.localize(LocalizedString.NoDaily, FillinUtil.of("h", hours, "m", minutes, "s", seconds)));
                }
            });
        });
    }
}

@Help(help=LocalizedString.CurrencyGiveCommandHelp, ehelp=LocalizedString.CurrencyGiveCommandExtendedHelp)
class CurrencyGiveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().cast(GuildMessageChannel.class)
        	.flatMap(channel->{
        		long uid = message.getAuthor().get().getId().asLong();
	        	ArgumentParser parser = new ArgumentParser(argument);
	        	if (parser.couldBeUserID()) {
	        		GuildAdapter gadapter = GuildAdapter.of(data.bot, channel);
	        		MemberAdapter madapter = MemberAdapter.of(gadapter, parser.eatUserID());
                    //Should I continue to allow sending money to users who have left?
        			
                    if (parser.isNumerical()) {
                    	if (madapter.getUserID()==uid)
                    		return Mono.error(new LocalizedException(LocalizedString.SelfSendMoney));
                    	
                    	if (madapter.getUserID()==data.bot.clientID)
                    		return channel.createMessage(data.localize(LocalizedString.SendMoneyThankYou));
                    		
                        int amount = (int) parser.eatNumerical();
                        MemberAdapter invoker = MemberAdapter.of(gadapter, uid);
                        
                        return invoker.getDocument().flatMap(pdoc->{
                        	DBObject pobj = pdoc.getObject();
                        	int ifeth = pobj.getOrDefaultInt("feth", 0);
                            if (amount<0)
                            	return Mono.error(new LocalizedException(LocalizedString.CurrencyStealing));
                            if (ifeth<amount)
                            	return Mono.error(new LocalizedException(LocalizedString.NotEnoughCurrency));
                            
                            return madapter.getDocument().flatMap(tdoc->{
                            	DBObject tobj = tdoc.getObject();
	                            pobj.set("feth", ifeth-amount);
	                            tobj.set("feth", tobj.getOrDefaultInt("feth", 0)+amount);
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

@Help(help=LocalizedString.CurrencyTopCommandHelp, ehelp=LocalizedString.CurrencyTopCommandExtendedHelp)
class CurrencyTopCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
    		long uid = message.getAuthor().get().getId().asLong();
    		
    		GuildAdapter gadapter = GuildAdapter.of(data.bot, channel);
    		MemberAdapter madapter = MemberAdapter.of(gadapter, uid);
    		
            int len = 5;
            LinkedList<DBDocument> lboard = new LinkedList<DBDocument>();
            AtomicReference<DBDocument> idoc = new AtomicReference<DBDocument>();
            AtomicInteger asize = new AtomicInteger();
            
            return gadapter.members().filter(Filters.gt("feth", 0)).rest().doOnNext(doc->{
            	//TODO: O(n) execution time
            	DBObject obj = doc.getObject();
            	if (obj.getOrDefaultLong("uid", -1L) == madapter.getUserID()) idoc.set(doc);
            		
            	long money = obj.getOrDefaultLong("feth", 0);
            	int i = 0;
                while (true) {
                    if (i==asize.get()) {
                        lboard.add(i, doc); break; //Insert user at end of lboard
                    } else {
                    	DBObject obj2 = lboard.get(i).getObject();
                		if (obj2.getOrDefaultLong("feth", 0)<money) {
                			lboard.add(i, doc); //Insert right before next lowest lboard value
                			break;
                		}
                    }
                    i++;
                }
                asize.incrementAndGet();
            }).then(Mono.just(true)).flatMap(_0->{
            	int xpos = -1;
            	if (idoc.get()!=null) xpos = lboard.indexOf(idoc.get());
            	
            	int pos = xpos;
            	int size = asize.get();

                ArrayList<EmbedFieldEntry> boardusers = new ArrayList<EmbedFieldEntry>();
                AtomicReference<Mono<?>> monos = new AtomicReference<Mono<?>>(Mono.empty());

                for (int i=0; i<((size<len)?size:len); i++) addToLeaderboard(i, size, lboard, monos, boardusers, gadapter, madapter, data);
                if (pos!=-1) {
	                if (pos-1 >= len) addToLeaderboard(pos-1, size, lboard, monos, boardusers, gadapter, madapter, data);
	                if (pos >= len)   addToLeaderboard(pos  , size, lboard, monos, boardusers, gadapter, madapter, data);
	                if (pos+1 >= len) addToLeaderboard(pos+1, size, lboard, monos, boardusers, gadapter, madapter, data);
                }

                return monos.get().flatMap(_1->{
                	return channel.createEmbed(embed->{
                        embed.setTitle("Currency Leaderboard");
                        boardusers.forEach(embedinfo->{
                            embed.addField(embedinfo.title, embedinfo.content, false);
                        });
                        if (pos!=-1) embed.setFooter("You rank #"+(pos+1), null);
                	});
                });
            });
        });
    }
    
    private void addToLeaderboard(int i, int size, LinkedList<DBDocument> lboard, AtomicReference<Mono<?>> monos,
    	ArrayList<EmbedFieldEntry> boardusers, GuildAdapter gadapter, MemberAdapter invoker, CommandData data) {
    	
        if (i>=size) return;
        MemberAdapter user = MemberAdapter.of(gadapter, lboard.get(i).getObject().getOrDefaultLong("uid", -1L));
        monos.set(monos.get().then(user.getDocument().flatMap(doc->{
	        long feth = doc.getObject().getOrDefaultLong("feth", 0);
	        if (feth<=0&&!(user==invoker)) return Mono.empty();
	        return user.getMember()
	        	.doOnNext(m->{
	        		boardusers.add(new EmbedFieldEntry("#"+(i+1)+(user.getUserID()==invoker.getUserID()?" (You)":""), data.safe(m.getDisplayName())+": "+feth+" feth"));
	        	}).onErrorResume(e->Mono.empty());
        }).onErrorResume(e->Mono.empty())));
    }
}

class EmbedFieldEntry {
    public String title;
    public String content;
    public EmbedFieldEntry(String title, String content) {
        this.title = title;
        this.content = content;
    }
}