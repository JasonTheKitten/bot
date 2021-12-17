package everyos.bot.luwu.run.command.modules.chatlink.link;

import everyos.bot.luwu.core.database.DBObject;

public class LinkEditSpecImp implements LinkEditSpec {

	private final DBObject dbObject;

	public LinkEditSpecImp(DBObject dbObject) {
		this.dbObject = dbObject;
	}

	@Override
	public void setRules(String rules) {
		dbObject.set("rules", rules);
	}

}
