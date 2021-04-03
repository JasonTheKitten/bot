package everyos.bot.luwu.run.command.modules.currency;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class FethServer extends Server {
	public FethServer(Connection connection, ChatGuild guild) {
		this(connection, guild, null);
	}

	protected FethServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<FethServerInfo> getCurrencyInfo() {
		return getGlobalDocument().map(document->{
			DBObject serverObject = document.getObject();
			DBObject currencyObject = serverObject.getOrCreateObject("feth", obj->{});
			 
			boolean currencyEnabled = currencyObject.getOrDefaultBoolean("dofeth", false);
			
			return new FethServerInfo(currencyEnabled);
		});
	}

	public static FethServerFactory type = new FethServerFactory();

	public Mono<Void> setCurrencyInfo(FethServerInfo currencyInfo) {
		return getGlobalDocument().flatMap(document->{
			DBObject serverObject = document.getObject();
			DBObject currencyObject = serverObject.getOrCreateObject("feth", obj->{});
			 
			currencyObject.set("dofeth", currencyInfo.getCurrencyEnabled());
			
			return document.save();
		});
	}
}
