package everyos.bot.luwu.run.command.modules.chatlink.user;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.database.DBObject;

public class LinkUserInfoImp implements LinkUserInfo {

	private final DBObject dbObject;

	public LinkUserInfoImp(BotEngine botEngine, DBObject dbObject) {
		this.dbObject = dbObject;
	}

	@Override
	public boolean isMuted() {
		return dbObject.getOrDefaultBoolean("muted", false);
	}

	@Override
	public boolean isOpted() {
		return dbObject.getOrDefaultBoolean("opted", false);
	}
	
	@Override
	public boolean isVerified() {
		return dbObject.getOrDefaultBoolean("verified", false);
	}

}
