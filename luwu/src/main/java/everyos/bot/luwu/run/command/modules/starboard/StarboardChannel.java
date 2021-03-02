package everyos.bot.luwu.run.command.modules.starboard;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardChannel extends Channel {
	protected StarboardChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}

	
	public Mono<StarboardInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new StarboardInfoImp(doc.getObject());
		});
	}
	
	private static class StarboardInfoImp implements StarboardInfo {
		private DBObject object;

		public StarboardInfoImp(DBObject object) {
			this.object = object;
		}

		@Override
		public EmojiID getStarEmoji() {
			return EmojiID.of(object.getOrDefaultString("star", null));
		}
		
		@Override
		public boolean enabled() {
			return object.has("star");
		}

		@Override
		public ChannelID getStarboardChannel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ChannelID[] getExcludedChannels() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Tuple<ChannelID, Integer>[] getChannelOverrides() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Tuple<Integer, EmojiID>[] getEmojiLevels() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
