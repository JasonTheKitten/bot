package everyos.discord.bot.command.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.TopEntityAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.TimeUtil;
import everyos.storage.database.DBDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO: Localize more
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
}

class CurrencyBalanceCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
        	String uid = null;
        	String iuid = null;
        	iuid = message.getAuthor().get().getId().asString();
        	
        	ArgumentParser parser = new ArgumentParser(argument);
        	
        	
        	if (parser.couldBeUserID()) {
        		uid = parser.eatUserID();
        	} else if (parser.isEmpty()) {
        		uid = iuid;
        	} else {
        		return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
        	}
            TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
            if (!teadapter.isOfGuild()) return Mono.empty(); //TODO
            MemberAdapter madapter = MemberAdapter.of((GuildAdapter) teadapter.getPrimaryAdapter(), uid); //TODO: Check uid exists
            
            String fuid = iuid;
            return madapter.getMember()
            	.flatMap(member->{
            		String targetnameraw = member.getDisplayName();
            		String targetname = targetnameraw+"'s";
            		if (member.getId().asString().equals(fuid)) targetname = "Your";
            		
            		AtomicInteger feth = new AtomicInteger();
                    madapter.getDocument().getObject(obj->feth.set(obj.getOrDefaultInt("feth", 0)));
                    if (feth.get() == 0) {
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
        	return Mono.create(sink->{
	            MemberAdapter.of(gadapter, message.getAuthor().orElse(null).getId().asString()).getDocument().getObject((obj, doc)->{
	            	long curtime = System.currentTimeMillis();
	                long time = obj.getOrDefaultLong("fethdaily", 0);
	                long timeleft = time-curtime;
	                if (timeleft<=0) {
	                	obj.set("fethdaily", System.currentTimeMillis()+(24*60*60*1000));
	                    obj.set("feth",  obj.getOrDefaultInt("feth", 0)+100);
	                    doc.save();
	                    sink.success(channel.createMessage(data.locale.localize(LocalizedString.ReceivedDaily, FillinUtil.of("feth", "100"))));
	                } else {
	                	String hours = String.valueOf(TimeUtil.getHours(timeleft, false));
	                	String minutes = String.valueOf(TimeUtil.getMinutes(timeleft, true));
	                	String seconds = String.valueOf(TimeUtil.getSeconds(timeleft, true));
	                	sink.success(channel.createMessage(data.locale.localize(LocalizedString.NoDaily,
	                		FillinUtil.of("h", hours, "m", minutes, "s", seconds))));
	                }
	            });
        	}).flatMap(m->(Mono<?>) m);
        });
    }
}

class CurrencyGiveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
        	.flatMap(channel->{
        		String uid = message.getAuthor().get().getId().asString();
	        	//madapter.getUserID().equals(id)
	        	ArgumentParser parser = new ArgumentParser(argument);
	        	if (parser.couldBeUserID()) {
	        		TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
	        		if (!teadapter.isOfGuild()) return null; //TODO
	        		GuildAdapter gadapter = (GuildAdapter) teadapter.getPrimaryAdapter();
	        		MemberAdapter madapter = MemberAdapter.of(gadapter, parser.eatUserID());
                    //Should I continue to allow sending money to users who have left?
        			
        			//TODO: Sending to bot should give thank you message
                    if (parser.isNumerical()) {
                        int amount = (int) parser.eatNumerical();
                        MemberAdapter invoker = MemberAdapter.of(gadapter, uid);
                        
                        AtomicReference<Mono<Message>> reference = new AtomicReference<Mono<Message>>();
                        
                        invoker.getDocument().getObject((pobj, pdoc)->{
                        	int ifeth = pobj.getOrDefaultInt("feth", 0);
                            if (amount<0) {
                            	reference.set(channel.createMessage(data.locale.localize(LocalizedString.CurrencyStealing)));
                            	return;
                            }
                            if (ifeth<amount) {
                            	reference.set(channel.createMessage(data.locale.localize(LocalizedString.NotEnoughCurrency)));
                            	return;
                            }
                            madapter.getDocument().getObject((tobj, tdoc)->{
	                            pobj.set("feth", ifeth-amount);
	                            pdoc.save();
	                            
	                            tobj.set("feth", tobj.getOrDefaultInt("feth", 0)+amount);
	                            tdoc.save();
	                            reference.set(channel.createMessage(data.locale.localize(LocalizedString.MoneySent)));
                            });
                        });
                        
                        return reference.get();
                    } else {
                        return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
                    }
        		} else {
        			return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
        		}
        });
    }
}

class CurrencyTopCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
        	.flatMap(channel->{
        		String uid = message.getAuthor().get().getId().asString();
        		
	            TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
	            if (!teadapter.isOfGuild()) return null; //TODO
        		GuildAdapter gadapter = (GuildAdapter) teadapter.getPrimaryAdapter();
        		MemberAdapter madapter = MemberAdapter.of(gadapter, uid);
        		
                int len = 5;
                LinkedList<DBDocument> lboard = new LinkedList<DBDocument>();
                AtomicInteger c = new AtomicInteger(0);
                
                DBDocument[] members = teadapter.getDocument().subcollection("members").all();
                for (DBDocument member: members) {
                    //TODO: O(n) execution time
                    member.getObject(obj->{
                    	int money = obj.getOrDefaultInt("feth", 0);
	                    for (int i=0; i<c.incrementAndGet(); i++) {
	                        if (i==lboard.size()) {
	                            lboard.add(i, member); break;
	                        } else {
	                        	int ic = i;
	                        	AtomicBoolean doBreak = new AtomicBoolean(false);
	                        	lboard.get(i).getObject(obj2->{
	                        		if (obj2.getOrDefaultInt("feth", 0)<money) {
	                        			lboard.add(ic, member);
	                        			doBreak.set(true);
	                        		}
	                        	});
	                        	if (doBreak.get()) break;
	                        }
	                    }
                    });
                };

                int pos = lboard.indexOf(madapter.getDocument());

                ArrayList<EmbedFieldEntry> boardusers = new ArrayList<EmbedFieldEntry>();
                ArrayList<Mono<?>> monos = new ArrayList<Mono<?>>();
	            int size = lboard.size();
	
	            for (int i=0; i<((size<len)?size:len); i++) addToLeaderboard(i, size, lboard, monos, boardusers, gadapter, madapter);
	            if (pos-1 >= len) addToLeaderboard(pos-1, size, lboard, monos, boardusers, gadapter, madapter);
	            if (pos >= len)   addToLeaderboard(pos  , size, lboard, monos, boardusers, gadapter, madapter);
	            if (pos+1 >= len) addToLeaderboard(pos+1, size, lboard, monos, boardusers, gadapter, madapter);
	
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
    
    private void addToLeaderboard(int i, int size, LinkedList<DBDocument> lboard, ArrayList<Mono<?>> monos, ArrayList<EmbedFieldEntry> boardusers, GuildAdapter gadapter, MemberAdapter invoker) {
        if (i>=size) return;
        MemberAdapter user = MemberAdapter.of(gadapter, lboard.get(i).getName());
        user.getDocument().getObject(obj->{
	        int feth = obj.getOrDefaultInt("feth", 0);
	        if (feth<=0&&!(user==invoker)) return;
	        Mono<?> mono = user.getMember()
	        	.doOnNext(m->{
	        		boardusers.add(new EmbedFieldEntry("#"+(i+1)+(user.equals(invoker)?" (You)":""), m.getDisplayName()+": "+feth+" feth")); //TODO: Filter
	        	});
	        monos.add(mono);
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