package everyos.bot.luwu.run.command.modules.role.autorole;

import java.util.Map;
import java.util.Optional;

import everyos.bot.chat4j.entity.ChatGuild;
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
			return new AutoroleInfo() {
				DBObject object = doc.getObject();
				
				@Override
				public Optional<RoleID> getMemberRole() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Optional<RoleID> getBotRole() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public RoleID[] getDefaultRoles() {
					// TODO Auto-generated method stub
					return null;
				}
				
			};
		});
	}
}
