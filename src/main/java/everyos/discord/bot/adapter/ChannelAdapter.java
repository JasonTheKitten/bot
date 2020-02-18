package everyos.discord.bot.adapter;

import java.util.HashMap;

import discord4j.core.object.entity.Channel.Type;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.filter.EveryoneFilter;
import everyos.discord.bot.filter.Filter;
import everyos.discord.bot.filter.FilterProvider;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.functional.Procedure;

public class ChannelAdapter {
	private MessageChannel channel;
	private Localization locale;
	private String guildID;
	private String channelID;
	private Filter filter;
	private String prefix;
	
	public ChannelAdapter(Type type, MessageChannel channel) {
		this(type, channel, null);
	}
	public ChannelAdapter(Type type, MessageChannel channel, Snowflake snowflake) {
		this.channel = channel;
		if (snowflake!=null) this.guildID = snowflake.asString();
		this.channelID = channel.getId().asString();
	}
	
	public void send(String msg, Procedure after) {
		channel.createMessage(msg).subscribe(message->{
			if (after!=null) {
				after.execute();
			}
		});
	}
	public void send(String msg) {
		send(msg, null);
	}
	
	public Filter getPreferredFilter() {
		if (filter == null) {
			Main.db.collection("channels").getIfPresent(channelID, channel -> {
				String filterstr = channel.getObject().getOrDefaultString("filter", null);
				if (filterstr!=null) filter = FilterProvider.of(filterstr);
				return filter!=null;
			}).elsedo(()->{
				if (guildID!=null) {
					Main.db.collection("guilds").getIfPresent(guildID, guildo->{
						String filterstr = guildo.getObject().getOrDefaultString("filter", null);
						if (filterstr!=null) filter = FilterProvider.of(filterstr);
					});
				}
				return filter!=null;
			}).elsedo(()->filter = EveryoneFilter.filter);
		}
		return this.filter;
	}
	
	public String getPreferredPrefix() { //TODO: String[] getPreferredPrefixes() ?
		if (filter == null) {
			Main.db.collection("channels").getIfPresent(channelID, channel -> {
				prefix = channel.getObject().getOrDefaultString("prefix", null);
				return prefix!=null;
			}).elsedo(()->{
				if (guildID!=null) {
					Main.db.collection("guilds").getIfPresent(guildID, guildo->{
						prefix = guildo.getObject().getOrDefaultString("prefix", null);
					});
				}
				return prefix!=null;
			}).elsedo(()->prefix = "*");
		}
		return prefix;
	}
	
	public String formatTextLocale(LocalizedString label, HashMap<String, String> fillins) {
		return formatTextLocale(getTextLocale(), label, fillins);
	}
	public String formatTextLocale(Localization locale, LocalizedString label, HashMap<String, String> fillins) {
		ObjectStore rtn = new ObjectStore();
		rtn.object = LocalizationProvider.localize(getTextLocale(), label);
		fillins.forEach((k,v)->{
			rtn.object = ((String) rtn.object).replace("${"+k+"}", v);
		});
		return ((String) rtn.object).replace("${#", "${"); //Just don't put hashtags in the fillin names
	}
	
	public Localization getTextLocale() {
		if (locale == null) {
			Main.db.collection("channels").getIfPresent(channelID, channel -> {
				String localestr = channel.getObject().getOrDefaultString("locale", null);
				if (localestr!=null) locale = LocalizationProvider.of(localestr);
				return locale!=null;
			}).elsedo(()->{
				if (guildID!=null) {
					Main.db.collection("guilds").getIfPresent(guildID, guildo->{
						String localestr = guildo.getObject().getOrDefaultString("locale", null);
						if (localestr!=null) locale = LocalizationProvider.of(localestr);
					});
				}
				return locale!=null;
			}).elsedo(()->locale = Localization.en_US);
		}
		return this.locale;
	}
	public void setTextLocale(Localization locale) {
		this.locale = locale;
	}
	
	public boolean shouldIgnoreUser() {
		return false;
	}
}
