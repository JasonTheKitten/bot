package everyos.discord.bot.adapter;

import java.util.HashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBDocument;

public class MessageAdapter implements IAdapter {
    public String messageID;

    private String channelID;
    private Message message;
    private String guildID;
    private Localization locale;

    private MessageAdapter(Message message) {
        this(message.getChannelId().asString(), message.getId().asString());
        this.message = message;
        onMessageSet();
    }

    private MessageAdapter(String channelID, String id) {
        this.channelID = channelID;
        this.messageID = id;
    }

    private void onMessageSet() {
        this.channelID = message.getChannelId().asString();
    }

    public void getChannelAdapter(Consumer<ChannelAdapter> func) {
        require(adapter -> func.accept(ChannelAdapter.of(adapter.channelID)));
    }

    private void getGuildAdapter(Consumer<GuildAdapter> func) {
    	if (guildID!=null) {
    		func.accept(GuildAdapter.of(guildID)); return;
    	}
        require(adapter -> {
    		message.getChannel().subscribe(channel->{
    			if (channel instanceof GuildChannel) {
	        		func.accept(GuildAdapter.of(((GuildChannel) channel).getGuildId().asString()));
    			} else func.accept(null);
        	});
        });
    }

    private void require(Consumer<MessageAdapter> after) {
        if (this.message!=null) {after.accept(this);}
        else Main.client.getMessageById(Snowflake.of(channelID), Snowflake.of(messageID)).subscribe(message -> {
            this.message = message;
            onMessageSet();
            after.accept(this);
        });
    };
    
    public void getSenderID(Consumer<String> func) {
    	require(madapter->{
    		User author = message.getAuthor().get();
    		if (author == null) {
    			func.accept("");
    		} else {
    			func.accept(author.getId().asString());
    		}
    	});
    }

    public static MessageAdapter of(Message message) {
        return new MessageAdapter(message);
    }

    public String[] getPreferredPrefix() {
        return new String[] { "*" };
    }

    public void setTextLocale(Localization locale) {
        this.locale = locale;
    }

    public void getTextLocale(@Nonnull Consumer<Localization> func) {
    	//TODO: Users should have their own text locales?
        if (locale != null) {
            func.accept(locale);
            return;
        }
        getChannelAdapter(adapter -> {
            adapter.getTextLocale(locale -> {
                func.accept(locale);
            }).elsedo(() -> {
                getGuildAdapter(gadapter -> {
                	if (gadapter==null) {
                		locale = Localization.en_US;
                        func.accept(locale);
                        return;
                	}
                    gadapter.getTextLocale(locale -> {
                        func.accept(locale);
                    }).elsedo(() -> {
                        locale = Localization.en_US;
                        func.accept(locale);
                    });
                });
            });
        });
    }

    public void formatTextLocale(LocalizedString label, HashMap<String, String> fillins, Consumer<String> func) {
        getTextLocale(locale -> {
            func.accept(formatTextLocale(locale, label, fillins));
        });
    }
    public void formatTextLocale(LocalizedString label, Consumer<String> func) {
        formatTextLocale(label, null, func);
    }
    public String formatTextLocale(Localization locale, @Nonnull LocalizedString label, HashMap<String, String> fillins) {
        ObjectStore rtn = new ObjectStore();
        rtn.object = LocalizationProvider.localize(locale, label);
        if (fillins != null)
            fillins.forEach((k, v) -> {
                rtn.object = ((String) rtn.object).replace("${" + k + "}", v);
            });
        return ((String) rtn.object).replace("${#", "${"); // Just don't put hashtags in the fillin names
    }
    
    public void getUserAdapter(Consumer<UserAdapter> func) {
        require(adapter->{
            adapter.message.getAuthor().ifPresent(author->{
                func.accept(UserAdapter.of(author.getId().asString()));
            });
        });
    }
    public void getMemberAdapter(Consumer<MemberAdapter> func) {
    	getUserAdapter(uadapter->
    		getTopEntityAdapter(gsadapter->
    			func.accept(MemberAdapter.of(gsadapter, uadapter))));
	}
    public void getTopEntityAdapter(Consumer<IAdapter> func) {
    	getUserAdapter(uadapter->{
	    	getGuildAdapter(gadapter->{
	    		if (gadapter == null) {
	    			getChannelAdapter(cadapter->func.accept(cadapter));
	    			return;
	    		}
	    		func.accept(gadapter);
	    	});
    	});
    }
    
    @Override public DBDocument getDocument() {
        return null; //Messages don't (currently) have an attached document
    }
}