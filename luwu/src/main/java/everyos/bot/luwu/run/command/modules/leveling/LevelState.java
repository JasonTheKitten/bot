package everyos.bot.luwu.run.command.modules.leveling;

import everyos.bot.luwu.util.Tuple;

public class LevelState {
	private static final int XP_BASE = 3;
	private static final double XP_SPEED = 1.35;
	
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
		return calculateLevelAndXP().getT2().getT1();
	}
	
	public long getXPToNextLevel() {
		return calculateLevelAndXP().getT2().getT2();
	}
	
	public int getLevel() {
		return calculateLevelAndXP().getT1();
	}
	
	private Tuple<Integer, Tuple<Long, Long>> calculateLevelAndXP() {
		int lvl = 0;
		long remaining = xp;
		long forNextLevel = XP_BASE;
		
		while (remaining>=forNextLevel) {
			lvl++;
			remaining-=forNextLevel;
			forNextLevel = (long)(forNextLevel*XP_SPEED);
		}
		
		return Tuple.of(lvl, Tuple.of(remaining, forNextLevel));
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
}
