package everyos.bot.luwu.core.entity;

import java.util.Objects;

import reactor.core.publisher.Mono;

public class MessageID {
	private ChannelID channelID;
	private long messageID;

	public MessageID(ChannelID channelID, long messageID) {
		this.channelID = channelID;
		this.messageID = messageID;
	}
	
	public ChannelID getChannel() {
		return this.channelID;
	}
	
	public Connection getConnection() {
		return channelID.getConnection();
	}
	
	public long getLong() {
		return this.messageID;
	}

	public Mono<Message> getMessage() {
		return getConnection().getChannelByID(channelID)
			.flatMap(channel->channel.getMessageByID(this));
	}
	
	@Override
	public boolean equals(Object o) {
		return
			(o instanceof MessageID) &&
			(((MessageID) o).getLong() == getLong());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(messageID);
	}
}
