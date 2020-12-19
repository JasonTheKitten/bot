package everyos.bot.luwu.run.command.modules.levelling;

public class LevelInfo {
	private boolean levellingEnabled;
	private long messageChannel;
	private String levelMessage;

	public LevelInfo(boolean levellingEnabled, long messageChannel, String levelMessage) {
		this.levellingEnabled = levellingEnabled;
		this.messageChannel = messageChannel;
		this.levelMessage = levelMessage;
	}
	
	public boolean getLevellingEnabled() {
		return levellingEnabled;
	}
	public long getMessageChannelID() {
		return messageChannel;
	}
	public String getLevelMessage() {
		return levelMessage;
	}
}
