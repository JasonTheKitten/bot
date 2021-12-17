package everyos.bot.luwu.run.command.modules.chatlink.channel;

import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.UserID;

public class LinkChannelEditSpecImp implements LinkChannelEditSpec {

	private DBObject dbObject;

	public LinkChannelEditSpecImp(DBObject dbObject) {
		this.dbObject = dbObject;
	}

	@Override
	public void setVerified(boolean verified) {
		dbObject.set("verified", verified);
	}

	@Override
	public void setOpted(boolean opted) {
		dbObject.set("opted", opted);
	}

	@Override
	public void addMutedUser(UserID user) {
		dbObject.getOrCreateArray("muted").add(user.getLong());
	}

	@Override
	public void removeMutedUser(UserID user) {
		dbObject.getOrCreateArray("muted").removeAll(user.getLong());
	}

	@Override
	public void setLinkID(long id) {
		dbObject.set("chatlinkid", id);
	}

}
