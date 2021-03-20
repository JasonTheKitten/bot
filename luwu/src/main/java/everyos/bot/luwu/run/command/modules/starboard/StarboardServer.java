package everyos.bot.luwu.run.command.modules.starboard;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBArray;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Server;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardServer extends Server {
	protected StarboardServer(Connection connection, ChatGuild server, Map<String, DBDocument> documents) {
		super(connection, server, documents);
	}

	
	public Mono<StarboardInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			return new StarboardInfoImp(doc.getObject());
		});
	}
	
	private class StarboardInfoImp implements StarboardInfo {
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
		public ChannelID getStarboardChannelID() {
			return new ChannelID(getConnection(), object.getOrDefaultLong("starc", -1L), getClient().getID());
		}

		@Override
		public ChannelID[] getExcludedChannels() {
			return new ChannelID[] {};
		}

		@SuppressWarnings("unchecked")
		@Override
		public Tuple<ChannelID, Integer>[] getChannelOverrides() {
			return new Tuple[] {};
		}

		@SuppressWarnings("unchecked")
		@Override
		public Tuple<Integer, EmojiID>[] getEmojiLevels() {
			DBArray starsArr = object.getOrCreateArray("stars");
			if (starsArr.getLength() == 0) {
				addDefaultStars(starsArr);
			}
			Tuple<Integer, EmojiID>[] stars = new Tuple[starsArr.getLength()/2];
			int j = 0;
			for (int i=0; i<starsArr.getLength(); i+=2) {
				stars[j++] = Tuple.of(starsArr.getInt(i), EmojiID.of(starsArr.getString(i+1)));
			}
			return stars;
		}

		@Override
		public Mono<Channel> getStarboardChannel() {
			return getStarboardChannelID().getChannel();
		}
	}
	
	private class StarboardEditSpecImp implements StarboardEditSpec {

		private DBObject object;

		public StarboardEditSpecImp(DBObject object) {
			this.object = object;
		}

		//TODO: Reset
		
		@Override
		public void setReaction(EmojiID id) {
			object.set("star", id.toString());
		}

		@Override
		public void setOutputChannel(ChannelID id) {
			object.set("starc", id.getLong());
		}

		@Override
		public StarboardInfo getInfo() {
			return new StarboardInfoImp(object);
		}

		@Override
		public void addChannelOverride(ChannelID channelID, int requiredStars) {
			
		}

		@Override
		public void addEmojiLevel(int level, EmojiID emoji) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeEmojiLevel(int level) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ignoreChannel(ChannelID channelID) {
			
		}

		@Override
		public void unignoreChannel(ChannelID channelID) {
			
		}

		@Override
		public void reset() {
			object.remove("star");
			object.remove("starc");
			object.remove("stars");
		}
	}

	public Mono<Void> edit(Consumer<StarboardEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			func.accept(new StarboardEditSpecImp(doc.getObject()));
			
			return doc.save();
		});
	}
	
	private void addDefaultStars(DBArray starsArr) {
		starsArr.add(3);
		starsArr.add("\u2b50");
		starsArr.add(5);
		starsArr.add("\uD83C\uDF1F");
	}
	
	public static StarboardServerFactory type = new StarboardServerFactory();
}
