package everyos.bot.luwu.run.command.modules.levelling;

public class LevelState {
	private long xp;
	private long timestamp;

	public LevelState(long xp, long timestamp) {
		this.xp = xp;
		this.timestamp = timestamp;
	}
	
	public long getXPTotal() {
		return this.xp;
	}
	
	public long getXPLeveled() {
		return 0;
	}
	
	public int getLevel() {
		return 0;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
}
