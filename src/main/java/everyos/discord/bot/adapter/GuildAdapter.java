package everyos.discord.bot.adapter;

import java.util.function.Consumer;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.filter.Filter;
import everyos.discord.bot.filter.FilterProvider;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.standards.GuildDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.OtherCase;

public class GuildAdapter {
    private String guildID;
    private Guild guild;
    private Localization locale;
    private Filter filter;
    private String prefix;

    ///
	//These constructors should only be called by the "of" command, or internally
	public GuildAdapter(Guild guild) {
        this(guild.getId().asString());
		this.guild = guild;
		onGuildSet();
    }
    public GuildAdapter(String guildID) {
        this.guildID = guildID;
    }
    ///

    private void onGuildSet() {}

    private void require(Consumer<GuildAdapter> adapter) {
        if (guild==null) {
            Main.client.getGuildById(Snowflake.of(guildID)).subscribe(guild->{
                this.guild = guild;
                onGuildSet();
                adapter.accept(this);
            });
            return;
        }
        adapter.accept(this);
    }

    public OtherCase getPreferredFilter(Consumer<Filter> func) {
		if (filter == null) {
			return Main.db.collection("guilds").getIfPresent(guildID, guild -> {
                String filterstr = guild.getObject().getOrDefaultString("locale", null);
                if (filter!=null) {
                    filter = FilterProvider.of(filterstr);
                    func.accept(filter);
                }
				return filter!=null;
            });
        }
        func.accept(filter);
        return new OtherCase(true);
    }
	
	public OtherCase getPreferredPrefix(Consumer<String> func) { //TODO: String[] getPreferredPrefixes() ?
		if (prefix == null) {
			return Main.db.collection("guilds").getIfPresent(guildID, guild -> {
				prefix = guild.getObject().getOrDefaultString("locale", null);
                if (prefix!=null) func.accept(prefix);
				return prefix!=null;
            });
        }
        func.accept(prefix);
        return new OtherCase(true);
	}
	
	public OtherCase getTextLocale(Consumer<Localization> func) {
		if (locale == null) {
			return Main.db.collection("guilds").getIfPresent(guildID, guild -> {
				String localestr = guild.getObject().getOrDefaultString("locale", null);
                if (localestr!=null) locale = LocalizationProvider.of(localestr);
                if (locale!=null) func.accept(locale);
				return locale!=null;
            });
        }
        func.accept(locale);
        return new OtherCase(true);
	}
	public void setTextLocale(Localization locale) {
		this.locale = locale; //TODO: Save to Main.db
	}
	
	public boolean shouldIgnoreUser() {
		return false;
    }

    public static GuildAdapter of(Guild guild) {
		ObjectStore rtn = new ObjectStore();
		Main.db.collection("guilds").getIfPresent(guild.getId().asString(), guildo -> {
			rtn.object = guildo.getMemoryOrNull("adapter");
			return rtn.object!=null;
		}).elsedo(()->{
			rtn.object = Main.db.collection("guilds").getOrSet(guild.getId().asString(), new GuildDocumentCreateStandard(guild)).getMemoryOrNull("adapter");
            //At this point, we assume GuildDocumentCreateStandard has created an adapter for us
            //If not, well...
		});
		
		return (GuildAdapter) rtn.object;
	}
    public static GuildAdapter of(String guildID) {
        ObjectStore rtn = new ObjectStore();
		Main.db.collection("guilds").getIfPresent(guildID, guildo -> {
			rtn.object = guildo.getMemoryOrNull("adapter");
			return rtn.object!=null;
		}).elsedo(()->{
            rtn.object = Main.db.collection("guilds").getOrSet(guildID, GuildDocumentCreateStandard.standard).getMemoryOrNull("adapter");
            //At this point, we assume GuildDocumentCreateStandard has created an adapter for us
            //If not, well...
        });
        
        return (GuildAdapter) rtn.object;
    }
}