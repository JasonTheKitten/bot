package everyos.bot.luwu.run.command.modules.chatlink.user;

import everyos.bot.luwu.core.database.DBObject;

public class LinkUserEditSpecImp implements LinkUserEditSpec {

	private DBObject dbObject;

	public LinkUserEditSpecImp(DBObject dbObject) {
		this.dbObject = dbObject;
	}

	@Override
	public void setMuted(boolean muted) {
		dbObject.set("muted", muted);
	}

	@Override
	public void setOpted(boolean opted) {
		dbObject.set("opted", opted);
	}
	
	@Override
	public void setVerified(boolean verified) {
		dbObject.set("verified", verified);
	}

}
