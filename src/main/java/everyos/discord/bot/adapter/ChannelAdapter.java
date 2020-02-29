package everyos.discord.bot.adapter;

import java.util.function.Consumer;

import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import everyos.discord.bot.Main;
import everyos.discord.bot.filter.Filter;
import everyos.discord.bot.filter.FilterProvider;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.standards.ChannelDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBCollection;
import everyos.storage.database.DBDocument;
import everyos.storage.database.OtherCase;

public class ChannelAdapter implements IAdapter {
    private MessageChannel channel;
    private Localization locale;
    private String guildID;
    private String channelID;
    private Filter filter;
    private String prefix;

    ///
    // These constructors should only be called by the "of" command, or internally
    public ChannelAdapter(MessageChannel channel) {
        this(channel.getId().asString());
        this.channel = channel;
        onChannelSet();
    }

    public ChannelAdapter(String channelID) {
        this.channelID = channelID;
    }
    ///

    private void onChannelSet() {
        if (channel instanceof GuildChannel) {
            this.guildID = ((GuildChannel) channel).getGuildId().asString();
        }
    }

    private void require(Consumer<ChannelAdapter> adapter) {
        if (channel == null) {
            Main.client.getChannelById(Snowflake.of(channelID)).subscribe(channel -> {
                this.channel = (MessageChannel) channel;
                onChannelSet();
                adapter.accept(this);
            });
            return;
        }
        adapter.accept(this);
    }

    private void requireGuildID(Consumer<ChannelAdapter> adapter) {
        if (guildID == null && !(channel instanceof GuildChannel)) {
            DBCollection collection = Main.db.collection("channels");
            collection.getIfPresent(this.channelID, channelo -> {
                this.guildID = channelo.getObject().getOrDefaultString("guild", null);
                if (this.guildID != null)
                    adapter.accept(this);
                return this.guildID != null;
            }).elsedo(() -> {
                require(__ -> {
                    if (this.guildID != null) {
                        collection.getOrSet(this.channelID, ChannelDocumentCreateStandard.standard).getObject().set("guild", this.guildID);
                        //TODO: Save?
                    }
                    adapter.accept(this);
                });
            });
            return;
        }
        adapter.accept(this);
    }
    
    public String getID() {
		return channelID;
	}

    public void send(String msg, Runnable after) {
        require(adapter -> adapter.channel.createMessage(msg).subscribe(message -> after.run()));
    }
    
    public void send(String msg, Consumer<Message> after) {
        require(adapter -> adapter.channel.createMessage(msg).subscribe(message -> after.accept(message)));
    }

    public void send(String msg) {
        send(msg, ()->{});
    }

    public void sendEmbed(Consumer<EmbedCreateSpec> embed, Runnable after) {
        require(adapter -> adapter.channel.createEmbed(embed).subscribe(message -> {
            if (after != null) {
                after.run();
            }
        }));
    }

    public void sendEmbed(Consumer<EmbedCreateSpec> embed) {
        sendEmbed(embed, null);
    }
    
    public void send(Consumer<MessageCreateSpec> mcs, Runnable after) {
    	require(adapter -> adapter.channel.createMessage(mcs).subscribe(message -> {
            if (after != null) {
                after.run();
            }
        }));
	}
    public void send(Consumer<MessageCreateSpec> mcs) {
		send(mcs, null);
	}

    public OtherCase getPreferredFilter(Consumer<Filter> func) {
        if (filter == null) {
            return Main.db.collection("channels").getIfPresent(channelID, channel -> {
                String filterstr = channel.getObject().getOrDefaultString("locale", null);
                if (filter != null) {
                    filter = FilterProvider.of(filterstr);
                    func.accept(filter);
                }
                return filter != null;
            });
        }
        func.accept(filter);
        return new OtherCase(true);
    }

    public OtherCase getPreferredPrefix(Consumer<String> func) { // TODO: String[] getPreferredPrefixes() ?
        if (prefix == null) {
            return Main.db.collection("channels").getIfPresent(channelID, channel -> {
                prefix = channel.getObject().getOrDefaultString("locale", null);
                if (prefix != null)
                    func.accept(prefix);
                return prefix != null;
            });
        }
        func.accept(prefix);
        return new OtherCase(true);
    }

    public OtherCase getTextLocale(Consumer<Localization> func) {
        if (locale == null) {
            return Main.db.collection("channels").getIfPresent(channelID, channel -> {
                String localestr = channel.getObject().getOrDefaultString("locale", null);
                if (localestr != null)
                    locale = LocalizationProvider.of(localestr);
                if (locale != null)
                    func.accept(locale);
                return locale != null;
            });
        }
        func.accept(locale);
        return new OtherCase(true);
    }

    public void setTextLocale(Localization locale) {
        this.locale = locale; // TODO: Save to Main.db
    }

    public boolean shouldIgnoreUser() {
        return false;
    }

    public void getGuildAdapter(Consumer<GuildAdapter> func) {
        requireGuildID(adapter -> {
            func.accept(GuildAdapter.of(guildID));
        });
    }

    public static ChannelAdapter of(MessageChannel channel) {
        ObjectStore rtn = new ObjectStore();
        Main.db.collection("channels").getIfPresent(channel.getId().asString(), channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = Main.db.collection("channels").getOrSet(channel.getId().asString(), ChannelDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new ChannelAdapter(channel);});
        });

        return (ChannelAdapter) rtn.object;
    }

    public static ChannelAdapter of(String channelID) {
        ObjectStore rtn = new ObjectStore();
        Main.db.collection("channels").getIfPresent(channelID, channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = Main.db.collection("channels").getOrSet(channelID, ChannelDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new ChannelAdapter(channelID);});
        });

        return (ChannelAdapter) rtn.object;
    }

    public void getMemberAdapter(Consumer<MemberAdapter> func, UserAdapter uadapter) {
        getGuildAdapter(gadapter->{
            func.accept(MemberAdapter.of((gadapter==null?this:gadapter), uadapter));
        });
    }

    @Override public DBDocument getDocument() {
        return Main.db.collection("channels").getOrSet(channelID, ChannelDocumentCreateStandard.standard);
    }
}
