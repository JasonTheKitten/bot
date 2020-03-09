package everyos.discord.bot.commands.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.IAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.object.Promise;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.MemberDocumentCreateStandard;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.TimeUtil;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class CurrencyCommand implements ICommand {
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

    @Override public void execute(Message message, MessageAdapter adapter, String argument) {
        if (argument.equals("")) argument = "balance";

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
	    adapter.getChannelAdapter(cadapter -> {
	        adapter.getTextLocale(locale->{
	        	ICommand command = getSubcommands(locale).get(cmd);
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
        	
        	Consumer<MemberAdapter> func = madapter->{
            	madapter.getDisplayName(targetnameraw->{
            		adapter.getSenderID(id->{
                		String targetname = targetnameraw+"'s";
                		if (madapter.getUserID().equals(id)) targetname = "Your";
                		
	                    int feth = madapter.getDocument().getObject().getOrDefaultInt("feth", 0);
	                    if (feth == 0) {
	                        adapter.formatTextLocale(LocalizedString.ZeroBalance,
	                        	FillinUtil.of("feth", String.valueOf(feth), "target", targetname),
	                        	str->cadapter.send(str)); 
	                        //I don't think this is actually reachable under normal use
	                    } else {
	                        adapter.formatTextLocale(LocalizedString.CurrentBalance, 
	                        	FillinUtil.of("feth", String.valueOf(feth), "target", targetname), 
	                        	str->cadapter.send(str));
	                    }
            		});
            	});
        	};
        	
        	ArgumentParser parser = new ArgumentParser(argument);
        	if (parser.couldBeUserID()) {
        		adapter.getTopEntityAdapter(gsadapter->{
	        		MemberDocumentCreateStandard.ifExists(gsadapter, parser.eatUserID(), func, ()->{
	        			adapter.formatTextLocale(LocalizedString.UnrecognizedUser, str->cadapter.send(str));
	                });
        		});
                return;
        	} else if (!parser.isEmpty()) {
        		adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
        		return;
        	}
            adapter.getMemberAdapter(func);
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
        return null;
    }
}

class CurrencyDailyCommand implements ICommand {
    @Override public void execute(Message message, MessageAdapter adapter, String argument) {
        adapter.getChannelAdapter(cadapter->{
            adapter.getMemberAdapter(madapter->{
            	DBObject obj = madapter.getDocument().getObject();
            	long curtime = System.currentTimeMillis();
                long time = obj.getOrDefaultLong("fethdaily", 0);
                long timeleft = time-curtime;
                if (timeleft<=0) {
                	obj.set("fethdaily", System.currentTimeMillis()+(24*60*60*1000));
                    obj.set("feth",  obj.getOrDefaultInt("feth", 0)+100);
                    madapter.getDocument().save();
                    adapter.formatTextLocale(LocalizedString.ReceivedDaily, FillinUtil.of("feth", "100"), str->cadapter.send(str));
                } else {
                	String hours = String.valueOf(TimeUtil.getHours(timeleft, false));
                	String minutes = String.valueOf(TimeUtil.getMinutes(timeleft, true));
                	String seconds = String.valueOf(TimeUtil.getSeconds(timeleft, true));
                	adapter.formatTextLocale(LocalizedString.NoDaily, FillinUtil.of("h", hours, "m", minutes, "s", seconds), str->cadapter.send(str));
                }
            });
        });
    }

    @Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }

    @Override public String getBasicUsage(Localization locale) {
        return " Credits the daily feth prize"; //TODO: Wait these should return LocalizedStrings
    }

    @Override public String getExtendedUsage(Localization locale) {
        return "Gives invoker 100 feth. Can only be run once every 24 hours";
    }

    @Override public CategoryEnum getCategory() {
        return null;
    }
}

class CurrencyGiveCommand implements ICommand {
    @Override public void execute(Message message, MessageAdapter adapter, String argument) {
        adapter.getChannelAdapter(cadapter->{
        	//madapter.getUserID().equals(id)
        	ArgumentParser parser = new ArgumentParser(argument);
        	if (parser.couldBeUserID()) {
        		adapter.getTopEntityAdapter(teadapter->{
	        		MemberDocumentCreateStandard.ifExists(teadapter, parser.eatUserID(), madapter->{
                        //Should I continue to allow sending money to users who have left?
	                    if (parser.isNumerical()) {
	                        int amount = (int) parser.eatNumerical();
	                        adapter.getMemberAdapter(invoker->{
	                            DBDocument doc = invoker.getDocument();
	                            DBDocument doc2 = madapter.getDocument();
                                int ifeth = doc.getObject().getOrDefaultInt("feth", 0);
                                if (amount<0) {
                                    adapter.formatTextLocale(LocalizedString.CurrencyStealing, str->cadapter.send(str));
                                    return;
                                }
	                            if (ifeth<amount) {
                                    adapter.formatTextLocale(LocalizedString.NotEnoughCurrency, str->cadapter.send(str));
                                    return;
	                            }
	                            doc.getObject().set("feth", ifeth-amount);
	                            doc.save();
	                            doc2.getObject().set("feth",  doc2.getObject().getOrDefaultInt("feth", 0)+amount);
	                            doc2.save();
	                            adapter.formatTextLocale(LocalizedString.MoneySent, str->cadapter.send(str));
	                        });
	                    } else {
	                        adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
	                    }
	        		}, ()->{
	        			adapter.formatTextLocale(LocalizedString.UnrecognizedUser, str->cadapter.send(str));
	        		});
        		}); 
        	} else {
                adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
        		return;
            }
        });
    }

    @Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }

    @Override public String getBasicUsage(Localization locale) {
        return "<user><amount> Give a user your feth";
    }

    @Override public String getExtendedUsage(Localization locale) {
        return "[user] The targeted user. Defaults to invoker.";
    }

    @Override public CategoryEnum getCategory() {
        return null;
    }
}

class CurrencyTopCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
        adapter.getChannelAdapter(cadapter->{
            adapter.getTopEntityAdapter(teadapter->{
                adapter.getMemberAdapter(madapter->{
                    int len = 5;
                    LinkedList<DBDocument> lboard = new LinkedList<DBDocument>();
                    AtomicInteger c = new AtomicInteger(0);
                    
                    DBDocument[] members = teadapter.getDocument().subcollection("members").all();
                    for (DBDocument member: members) {
                        //TODO: O(n) execution time
                        int money = member.getObject().getOrDefaultInt("feth", 0);
                        for (int i=0; i<c.incrementAndGet(); i++) {
                            if (i==lboard.size()||lboard.get(i).getObject().getOrDefaultInt("feth", 0)<money) {
                                lboard.add(i, member); break;
                            }
                        }
                    };

                    int pos = lboard.indexOf(madapter.getDocument());

                    ArrayList<EmbedFieldEntry> boardusers = new ArrayList<EmbedFieldEntry>();
                    Promise promise = new Promise(()->{
                        cadapter.sendEmbed(embed->{
                            embed.setTitle("Currency Leaderboard");
                            boardusers.forEach(embedinfo->{
                                embed.addField(embedinfo.title, embedinfo.content, false);
                            });
                            if (lboard.contains(madapter.getDocument())) //TODO: Don't rely on a consistent doc
                                embed.setFooter("You rank #"+(pos+1), null);
                        });
                    });

                    int size = lboard.size();

                    for (int i=0; i<((size<len)?size:len); i++) addToLeaderboard(i, size, lboard, boardusers, promise, teadapter, madapter);
                    if (pos-1 >= len) addToLeaderboard(pos-1, size, lboard, boardusers, promise, teadapter, madapter);
                    if (pos >= len)   addToLeaderboard(pos  , size, lboard, boardusers, promise, teadapter, madapter);
                    if (pos+1 >= len) addToLeaderboard(pos+1, size, lboard, boardusers, promise, teadapter, madapter);

                    promise.ready();
                });
            });
        });
    }
    
    private void addToLeaderboard(int i, int size, LinkedList<DBDocument> lboard, ArrayList<EmbedFieldEntry> boardusers, Promise promise, IAdapter teadapter, MemberAdapter invoker) {
        if (i>=size) return;
        promise.createResolver();
        MemberAdapter user = MemberAdapter.of(teadapter, lboard.get(i).getName());
        int feth = user.getDocument().getObject().getOrDefaultInt("feth", 0);
        if (feth<=0&&!(user==invoker)) return;
        user.getDisplayName(name->{
            boardusers.add(new EmbedFieldEntry("#"+(i+1)+(user==invoker?" (You)":""), name+": "+feth+" feth")); //TODO: Filter
            promise.resolve();
        });
    }

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	@Override public CategoryEnum getCategory() { return null; }
}

class EmbedFieldEntry {
    public String title;
    public String content;
    public EmbedFieldEntry(String title, String content) {
        this.title = title;
        this.content = content;
    }
}