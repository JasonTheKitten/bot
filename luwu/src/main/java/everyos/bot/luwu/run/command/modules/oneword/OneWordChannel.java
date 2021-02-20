package everyos.bot.luwu.run.command.modules.oneword;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.UserID;
import reactor.core.publisher.Mono;

public class OneWordChannel extends Channel {
	protected OneWordChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}
	
	public Mono<Void> reset() {
		return getGlobalDocument().flatMap(channelDoc->{
			DBObject channelObj = channelDoc.getObject();
			channelObj.set("type", "oneword");
			DBObject channelData = channelObj.getOrCreateObject("data", obj->{});
			channelData.set("sentence", "");
			channelData.set("lastuser", -1L);
					
			return channelDoc.save();
		});
	}

	public Mono<OneWordInfo> getInfo() {
		return getGlobalDocument().map(channelDoc->{
			DBObject channelData = channelDoc.getObject().getOrCreateObject("data", obj->{});

			return new OneWordInfo() {
				@Override
				public String getMessage() {
					return channelData.getOrDefaultString("sentence", "");
				}
			
				@Override
				public UserID getLastUser() {
					return new UserID(getConnection(), channelData.getOrDefaultLong("lastuser", -1L));
				}
			};
		});
	}
	
	public Mono<Void> edit(Consumer<OneWordEditSpec> func) {
		return getGlobalDocument().flatMap(channelDoc->{
			DBObject channelData = channelDoc.getObject().getOrCreateObject("data", obj->{});

			func.accept(new OneWordEditSpec() {
				@Override
				public void setMessage(String message) {
					channelData.set("sentence", delimit(message));
				}
			
				@Override
				public void setLastUser(UserID id) {
					channelData.set("lastuser", id.getLong());
				}
			});
			
			return channelDoc.save();
		}).then();
	}
	
	public String delimit(String message) {
		int length = message.length();
		int begin = length>1750?length-1750:0;
		
		return message.substring(begin, length);
	}
	
	public static final OneWordChannelFactory type = new OneWordChannelFactory();
}
