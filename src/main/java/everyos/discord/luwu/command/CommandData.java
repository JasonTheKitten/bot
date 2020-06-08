package everyos.discord.luwu.command;

import java.util.HashMap;

import discord4j.core.event.domain.message.MessageEvent;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.filter.EveryoneFilter;
import everyos.discord.luwu.localization.LocalizationProvider;
import everyos.discord.luwu.localization.LocalizedString;

public class CommandData {
    public LocalizationProvider locale;
    public BotInstance bot;
	public IGroupCommand usercase;
	public IGroupCommand channelcase;
	public MessageEvent event;
	public String[] prefixes;

    public CommandData(BotInstance bot) {
        this.bot = bot;
    }

	public String safe(LocalizedString str, HashMap<String, String> fillins) {
        return safe(locale.localize(str, fillins));
    }
    public String safe(LocalizedString str) {
        return safe(locale.localize(str));
    }
    public String safe(String text) {
        return (new EveryoneFilter()).filter(text);
    }

	public String localize(LocalizedString label) {
		return locale.localize(label);
	}

	public String localize(LocalizedString label, HashMap<String, String> fillins) {
		return locale.localize(label, fillins);
	}
}