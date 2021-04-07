package everyos.bot.luwu.run.command.modules.music.playlist.internal;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.UserFactory;
import reactor.core.publisher.Mono;

public class PlaylistUserFactory implements UserFactory<PlaylistUser> {

	@Override
	public Mono<PlaylistUser> create(Connection connection, ChatUser user, Map<String, DBDocument> documents) {
		return Mono.just(new PlaylistUser(connection, user, documents));
	}

}
