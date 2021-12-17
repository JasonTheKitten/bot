package everyos.bot.luwu.run.command.modules.chatlink.link;

import java.util.Optional;

import everyos.bot.luwu.core.database.DBObject;

public class LinkInfoImp implements LinkInfo {

	private final DBObject dbObject;

	public LinkInfoImp(DBObject dbObject) {
		this.dbObject = dbObject;
	}

	@Override
	public Optional<String> getRules() {
		return Optional.ofNullable(dbObject.getOrDefaultString("rules", null));
	}

	@Override
	public boolean isAutoVerify() {
		return dbObject.getOrDefaultBoolean("autoverify", false);
	}

}
