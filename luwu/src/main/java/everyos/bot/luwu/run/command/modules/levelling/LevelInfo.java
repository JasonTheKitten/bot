package everyos.bot.luwu.run.command.modules.levelling;

import everyos.bot.luwu.core.entity.ChannelID;

public class LevelInfo {
	private boolean levellingEnabled;
	private ChannelID messageChannel;
	private String levelMessage;

	public LevelInfo(boolean levellingEnabled, ChannelID channelID, String levelMessage) {
		this.levellingEnabled = levellingEnabled;
		this.messageChannel = channelID;
		this.levelMessage = levelMessage;
	}
	
	public boolean getLevellingEnabled() {
		return levellingEnabled;
	}
	public ChannelID getMessageChannelID() {
		return messageChannel;
	}
	public String getLevelMessage() {
		return levelMessage;
	}
}
