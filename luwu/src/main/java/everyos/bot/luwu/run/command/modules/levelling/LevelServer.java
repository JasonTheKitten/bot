package everyos.bot.luwu.run.command.modules.levelling;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class LevelServer extends Server {
	//TODO: levelrole info
	public LevelServer(Connection connection, ChatGuild guild) {
		this(connection, guild, null);
	}

	protected LevelServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<LevelInfo> getLevelInfo() {
		return getServerDocument().map(document->{
			DBObject serverObject = document.getObject();
			DBObject levelObject = serverObject.getOrCreateObject("levelling", obj->{});
			 
			boolean levellingEnabled = levelObject.getOrDefaultBoolean("dolvl", false);
			long levelMessageChannelID = levelObject.getOrDefaultLong("cid", 0L);
			String levelMessage = levelObject.getOrDefaultString("msg", null);
			
			return new LevelInfo(levellingEnabled, new ChannelID(getConnection(), levelMessageChannelID), levelMessage);
		});
	}

	public static LevelServerFactory type = new LevelServerFactory();

	public Mono<Void> setLevelInfo(LevelInfo levelInfo) {
		return getServerDocument().flatMap(document->{
			DBObject serverObject = document.getObject();
			DBObject levelObject = serverObject.getOrCreateObject("levelling", obj->{});
			 
			levelObject.set("dolvl", levelInfo.getLevellingEnabled());
			levelObject.set("cid", levelInfo.getMessageChannelID().getLong());
			levelObject.set("msg", levelInfo.getLevelMessage());
			
			return document.save();
		});
	}
}
