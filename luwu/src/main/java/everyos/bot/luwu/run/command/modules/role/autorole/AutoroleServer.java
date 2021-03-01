package everyos.bot.luwu.run.command.modules.role.autorole;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBArray;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class AutoroleServer extends Server {
	public static AutoroleServerFactory type = new AutoroleServerFactory();

	public AutoroleServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<AutoroleInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new AutoroleInfoImp(doc.getObject());
		});
	}

	public Mono<Void> edit(Function<AutoroleEditSpec, Mono<Void>> func) {
		return getGlobalDocument().flatMap(doc->{
			return func.apply(new AutoroleEditSpec() {
				DBObject object = doc.getObject();

				@Override
				public void setUserRole(RoleID role) {
					object.set("userrole", role.getLong());
				}

				@Override
				public void setBotRole(RoleID role) {
					object.set("botrole", role.getLong());
				}

				@Override
				public void removeRole(RoleID role) {
					DBArray defaultRoles = object.getOrCreateArray("aroles");
					
					defaultRoles.removeAll(role.getLong());
					if (object.getOrDefaultLong("userrole", -1L) == role.getLong()) {
						object.remove("userrole");
					}
					if (object.getOrDefaultLong("botrole", -1L) == role.getLong()) {
						object.remove("botrole");
					}
				}

				@Override
				public void addDefaultRole(RoleID role) {
					removeRole(role);
					
					DBArray defaultRoles = object.getOrCreateArray("aroles");
					defaultRoles.add(role.getLong());
				}

				@Override
				public AutoroleInfo getInfo() {
					return new AutoroleInfoImp(object);
				}

				@Override
				public void reset() {
					object.remove("aroles");
					object.remove("botrole");
					object.remove("userrole");
				}
				
			}).then(doc.save());
		});
	}
	
	private class AutoroleInfoImp implements AutoroleInfo {
		private DBObject object;

		public AutoroleInfoImp(DBObject object) {
			this.object = object;
		}
		
		@Override
		public Optional<RoleID> getUserRole() {
			if (!object.has("userrole")) {
				return Optional.empty();
			}
			long id = object.getOrDefaultLong("userrole", -1L);
			return Optional.of(new RoleID(getConnection(), id));
		}

		@Override
		public Optional<RoleID> getBotRole() {
			if (!object.has("botrole")) {
				return Optional.empty();
			}
			long id = object.getOrDefaultLong("botrole", -1L);
			return Optional.of(new RoleID(getConnection(), id));
		}

		@Override
		public RoleID[] getDefaultRoles() {
			DBArray droles = object.getOrCreateArray("aroles");
			RoleID[] defaultRoles = new RoleID[droles.getLength()];
			for (int i=0; i<droles.getLength(); i++) {
				defaultRoles[i] = new RoleID(getConnection(), droles.getLong(i));
			}
			return defaultRoles;
		}
		
		@Override
		public boolean hasRole(RoleID id) {
			DBArray droles = object.getOrCreateArray("aroles");
			if (droles.contains(id.getLong())) {
				return true;
			}
			
			if (object.getOrDefaultLong("userrole", -1L) == id.getLong()) {
				return true;
			}
			if (object.getOrDefaultLong("botrole", -1L) == id.getLong()) {
				return true;
			}
				
			return false;
		}
	};
}
