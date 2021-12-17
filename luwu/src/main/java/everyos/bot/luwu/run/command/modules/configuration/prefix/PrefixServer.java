package everyos.bot.luwu.run.command.modules.configuration.prefix;

import java.util.Map;
import java.util.function.Function;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBArray;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import everyos.bot.luwu.core.entity.UserID;
import reactor.core.publisher.Mono;

public class PrefixServer extends Server {

	public PrefixServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}

	public Mono<PrefixServerInfo> getInfo() {
		return getConnection().getSelfAsUser().zipWith(getGlobalDocument())
				.map(tup->new PrefixServerInfoImp(tup.getT2().getObject(), tup.getT1().getID()));
	}
	
	public static Mono<PrefixServerInfo> empty(Connection connection) {
		return connection.getSelfAsUser()
			.map(self->new EmptyPrefixServerInfoImp(self.getID()));
	}
	
	public Mono<Void> edit(Function<PrefixServerEditSpec, Mono<Void>> func) {
		return getConnection().getSelfAsUser().zipWith(getGlobalDocument())
			.flatMap(tup->{
				return func.apply(new PrefixServerEditSpecImp(tup.getT2().getObject(), tup.getT1().getID()))
					.then(tup.getT2().save());
			});
	}
	
	private class PrefixServerInfoImp implements PrefixServerInfo {
		private DBObject object;

		public PrefixServerInfoImp(DBObject object, UserID botID) {
			this.object = object;
		}

		@Override
		public String[] getPrefixes() {
			DBArray prefixArray = object.getOrCreateArray("prefixes");
			if (prefixArray.getLength() == 0) {
				return getDefaultPrefixes();
			}
			String[] prefixes = new String[prefixArray.getLength()];
			for (int i = 0; i < prefixArray.getLength(); i++) {
				prefixes[i] = prefixArray.getString(i);
			}
			return prefixes;
		}	
	}
	
	private static class EmptyPrefixServerInfoImp implements PrefixServerInfo {
		public EmptyPrefixServerInfoImp(UserID botID) {
			
		}

		@Override
		public String[] getPrefixes() {
			return getDefaultPrefixes();
		}	
	}
	
	private class PrefixServerEditSpecImp implements PrefixServerEditSpec {
		private DBObject object;
		private UserID botID;

		public PrefixServerEditSpecImp(DBObject object, UserID botID) {
			this.object = object;
			this.botID = botID;
		}
		
		@Override
		public void addPrefix(String prefix) {
			DBArray prefixes = object.getOrCreateArray("prefixes");
			if (prefixes.getLength() == 0) {
				for (String dprefix: getDefaultPrefixes()) {
					prefixes.add(dprefix);
				}
			}
			prefixes.add(prefix);
		}
	
		@Override
		public void removePrefix(String prefix) {
			DBArray prefixes = object.getOrCreateArray("prefixes");
			if (prefixes.getLength() == 0) {
				for (String dprefix: getDefaultPrefixes()) {
					prefixes.add(dprefix);
				}
			}
			prefixes.removeAll(prefix);
		}
	
		@Override
		public void resetPrefix() {
			object.remove("prefixes");
		}
		
		@Override
		public PrefixServerInfo getInfo() {
			return new PrefixServerInfoImp(object, botID);
		}
	}
	
	private static String[] getDefaultPrefixes() {
		//TODO: Perhaps read this from JSON?
		return new String[] {
			"luwu ", "Luwu ", "LUWU "
		};
	}
	
	public static PrefixServerFactory type = new PrefixServerFactory();
	
}
