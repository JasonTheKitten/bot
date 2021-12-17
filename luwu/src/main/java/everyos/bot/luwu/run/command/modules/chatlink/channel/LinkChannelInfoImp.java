package everyos.bot.luwu.run.command.modules.chatlink.channel;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.run.command.modules.chatlink.link.Link;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkUtil;
import reactor.core.publisher.Mono;

public class LinkChannelInfoImp implements LinkChannelInfo {

	private final BotEngine botEngine;
	private final DBObject dbObject;

	public LinkChannelInfoImp(BotEngine botEngine, DBObject dbObject) {
		this.botEngine = botEngine;
		this.dbObject = dbObject;
	}

	@Override
	public long getLinkID() {
		return dbObject.getOrDefaultLong("chatlinkid", -1L);
	}

	@Override
	public boolean isVerified() {
		return dbObject.getOrDefaultBoolean("verified", false);
	}

	@Override
	public boolean isOpted() {
		return dbObject.getOrDefaultBoolean("opted", false);
	}

	@Override
	public boolean isUserMuted(UserID userID) {
		// TODO: Connection ID
		return dbObject.getOrCreateArray("muted").contains(userID.getLong());
	}

	@Override
	public Mono<Link> getLink() {
		return LinkUtil.getByID(botEngine, getLinkID());
	}

}
