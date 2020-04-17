package everyos.discord.bot.command.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.TopEntityAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.TimeUtil;
import everyos.storage.database.DBDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO: Localize more
@Help(help=LocalizedString.CurrencyCommandHelp, ehelp = LocalizedString.CurrencyCommandExtendedHelp, category=CategoryEnum.Fun)
public class CurrencyCommand implements IGroupCommand {
    private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public CurrencyCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand balanceCommand = new CurrencyBalanceCommand();
        ICommand dailyCommand = new CurrencyDailyCommand();
        ICommand giveCommand = new CurrencyGiveCommand();
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
        if (argument.equals("")) argument = "balance";

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null)
        	return message.getChannel().flatMap(c->c.createMessage(data.locale.localize(LocalizedString.NoSuchSubcommand)));
	    
        return command.execute(message, data, arg);
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

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
            MemberAdapter madapter = MemberAdapter.of(GuildAdapter.of(data.shard, channel), uid); //TODO: Check uid exists
            
            long fuid = iuid;
            return madapter.getMember()
            	.flatMap(member->{
            		String targetnameraw = member.getDisplayName();
            		String targetname = targetnameraw+"'s";
            		if (member.getId().asLong()==fuid) targetname = "Your";
            		
            		int feth = madapter.getData(obj->obj.getOrDefaultInt("feth", 0));
                    if (feth == 0) {
                        return channel.createMessage(data.locale.localize(LocalizedString.ZeroBalance,
                        	FillinUtil.of("feth", String.valueOf(feth), "target", targetname))); 
                        //I don't think this is actually reachable under normal use
                    } else {
                    	return channel.createMessage(data.locale.localize(LocalizedString.CurrentBalance, 
                        	FillinUtil.of("feth", String.valueOf(feth), "target", targetname)));
                    }
            	});
        });
    }
}

class CurrencyDailyCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
        	TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
        	if (!teadapter.isOfGuild()) return Mono.empty();
        	GuildAdapter gadapter = (GuildAdapter) teadapter.getPrimaryAdapter();
            return MemberAdapter.of(gadapter, message.getAuthor().orElse(null).getId().asLong()).getData((obj, doc)->{
            	long curtime = System.currentTimeMillis();
                long time = obj.getOrDefaultLong("fethdaily", 0);
                long timeleft = time-curtime;
                if (timeleft<=0) {
                	obj.set("fethdaily", System.currentTimeMillis()+(24*60*60*1000));
                    obj.set("feth",  obj.getOrDefaultInt("feth", 0)+100);
                    doc.save();
                    return channel.createMessage(data.locale.localize(LocalizedString.ReceivedDaily, FillinUtil.of("feth", "100")));
                } else {
                	String hours = String.valueOf(TimeUtil.getHours(timeleft, false));
                	String minutes = String.valueOf(TimeUtil.getMinutes(timeleft, true));
                	String seconds = String.valueOf(TimeUtil.getSeconds(timeleft, true));
                	return channel.createMessage(data.locale.localize(LocalizedString.NoDaily, FillinUtil.of("h", hours, "m", minutes, "s", seconds)));
                }
            });
        });
    }
}

class CurrencyGiveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().cast(GuildMessageChannel.class)
        	.flatMap(channel->{
        		long uid = message.getAuthor().get().getId().asLong();
	        	ArgumentParser parser = new ArgumentParser(argument);
	        	if (parser.couldBeUserID()) {
	        		GuildAdapter gadapter = GuildAdapter.of(data.shard, channel);
	        		MemberAdapter madapter = MemberAdapter.of(gadapter, parser.eatUserID());
                    //Should I continue to allow sending money to users who have left?
        			
                    if (parser.isNumerical()) {
                    	if (madapter.getMemberID()==uid)
                    		return Mono.error(new LocalizedException(LocalizedString.SelfSendMoney));
                    	
                    	if (madapter.getMemberID()==data.bot.clientID)
                    		return channel.createMessage(data.localize(LocalizedString.SendMoneyThankYou));
                    		
                        int amount = (int) parser.eatNumerical();
                        MemberAdapter invoker = MemberAdapter.of(gadapter, uid);
                        
                        return invoker.getData((pobj, pdoc)->{
                        	int ifeth = pobj.getOrDefaultInt("feth", 0);
                            if (amount<0)
                            	return Mono.error(new LocalizedException(LocalizedString.CurrencyStealing));
                            if (ifeth<amount)
                            	return Mono.error(new LocalizedException(LocalizedString.NotEnoughCurrency));
                            
                            return madapter.getData((tobj, tdoc)->{
	                            pobj.set("feth", ifeth-amount);
	                            pdoc.save();
	                            
	                            tobj.set("feth", tobj.getOrDefaultInt("feth", 0)+amount);
	                            tdoc.save();
	                            return channel.createMessage(data.locale.localize(LocalizedString.MoneySent));
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

class CurrencyTopCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
    		long uid = message.getAuthor().get().getId().asLong();
    		
    		GuildAdapter gadapter = GuildAdapter.of(data.shard, channel);
    		MemberAdapter madapter = MemberAdapter.of(gadapter, uid);
    		
            int len = 5;
            LinkedList<DBDocument> lboard = new LinkedList<DBDocument>();
            AtomicInteger c = new AtomicInteger(0);
            
            DBDocument[] members = gadapter.getDocument().subcollection("members").all();
            for (DBDocument member: members) {
                //TODO: O(n) execution time
                member.getObject(obj->{
                	int money = obj.getOrDefaultInt("feth", 0);
                    for (int i=0; i<c.incrementAndGet(); i++) {
                        if (i==lboard.size()) {
                            lboard.add(i, member); break;
                        } else {
                        	int ic = i;
                        	if (lboard.get(i).getObject(obj2->{
                        		if (obj2.getOrDefaultInt("feth", 0)<money) {
                        			lboard.add(ic, member);
                        			return true;
                        		}
                        		return false;
                        	})) break;
                        }
                    }
                });
            };

            int pos = lboard.indexOf(madapter.getDocument());

            ArrayList<EmbedFieldEntry> boardusers = new ArrayList<EmbedFieldEntry>();
            ArrayList<Mono<?>> monos = new ArrayList<Mono<?>>();
            int size = lboard.size();

            for (int i=0; i<((size<len)?size:len); i++) addToLeaderboard(i, size, lboard, monos, boardusers, gadapter, madapter, data);
            if (pos-1 >= len) addToLeaderboard(pos-1, size, lboard, monos, boardusers, gadapter, madapter, data);
            if (pos >= len)   addToLeaderboard(pos  , size, lboard, monos, boardusers, gadapter, madapter, data);
            if (pos+1 >= len) addToLeaderboard(pos+1, size, lboard, monos, boardusers, gadapter, madapter, data);

            return Flux.just(monos.toArray())
        		.flatMap(m->(Mono<?>) m)
            	.last()
            	.flatMap(o->{
            		return channel.createEmbed(embed->{
                        embed.setTitle("Currency Leaderboard");
                        boardusers.forEach(embedinfo->{
                            embed.addField(embedinfo.title, embedinfo.content, false);
                        });
                        if (lboard.contains(madapter.getDocument())) //TODO: Don't rely on a consistent doc
                            embed.setFooter("You rank #"+(pos+1), null);
                    });
            	});
	        });
    }
    
    private void addToLeaderboard(int i, int size, LinkedList<DBDocument> lboard, ArrayList<Mono<?>> monos,
    	ArrayList<EmbedFieldEntry> boardusers, GuildAdapter gadapter, MemberAdapter invoker, CommandData data) {
    	
        if (i>=size) return;
        MemberAdapter user = MemberAdapter.of(gadapter, lboard.get(i).getName());
        user.getData(obj->{
	        int feth = obj.getOrDefaultInt("feth", 0);
	        if (feth<=0&&!(user==invoker)) return null;
	        Mono<Member> mono = user.getMember()
	        	.doOnNext(m->{
	        		boardusers.add(new EmbedFieldEntry("#"+(i+1)+(user.equals(invoker)?" (You)":""), data.safe(m.getDisplayName())+": "+feth+" feth"));
	        	});
	        monos.add(mono);
	        
	        return null;
        });
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