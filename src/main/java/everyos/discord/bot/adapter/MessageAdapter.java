package everyos.discord.bot.adapter;

import java.util.HashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import discord4j.core.object.entity.Message;
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
    private String guildID;
    private Message message;
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
        require(adapter -> func.accept(GuildAdapter.of(adapter.guildID)));
    }

    private void require(Consumer<MessageAdapter> after) {
        if (this.message!=null) {after.accept(this);}
        else Main.client.getMessageById(Snowflake.of(channelID), Snowflake.of(messageID)).subscribe(message -> {
            this.message = message;
            onMessageSet();
            after.accept(this);
        });
    };

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
        if (locale != null) {
            func.accept(locale);
            return;
        }
        getChannelAdapter(adapter -> {
            adapter.getTextLocale(locale -> {
                func.accept(locale);
            }).elsedo(() -> {
                getGuildAdapter(gadapter -> {
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

    @Override public DBDocument getDocument() {
        return null; //Messages don't (currently) have an attached document
    }
}