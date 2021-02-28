package everyos.bot.luwu.run.command.modules.role.role;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class RoleServer extends Server {
	public static RoleServerFactory type = new RoleServerFactory();

	public RoleServer(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		super(connection, guild, documents);
	}
	
	public Mono<RoleInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new RoleInfo() {
				DBObject roles = doc.getObject().getOrCreateObject("roles", obj->{});

				@Override
				public Optional<RoleID> getRole(String name) {
					long id = roles.getOrDefaultLong(name, -1L);
					if (id==-1L) {
						return Optional.empty();
					}
					return Optional.of(new RoleID(getConnection(), id));
				}

				@Override
				public Map<String, RoleID> getAvailableRoles() {
					Map<String, RoleID> rolesmap = new HashMap<>();
					for (String key: roles.getKeys()) {
						long id = roles.getOrDefaultLong(key, -1L);
						rolesmap.put(key, new RoleID(getConnection(), id));
					}
					
					return rolesmap;
				}
			};
		});
	}
	
	public Mono<Void> edit(Consumer<RoleEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			func.accept(new RoleEditSpec() {
				DBObject roles = doc.getObject().getOrCreateObject("roles", obj->{});

				@Override
				public void setRole(String name, RoleID id) {
					roles.set(name, id.getLong());
				}

				@Override
				public void reset() {
					doc.getObject().remove("roles");
					roles = doc.getObject().getOrCreateObject("roles", obj->{});
				}

				@Override
				public void removeRole(String roleName) {
					roles.remove(roleName);
				}
			});
			
			return doc.save();
		});
	}
}
