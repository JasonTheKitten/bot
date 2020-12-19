package everyos.bot.luwu.core.entity.imp;

import java.util.function.Consumer;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class ChannelTextInterfaceImp implements ChannelTextInterface {
	private Channel channel;
	private ChatChannelTextInterface textGrip;

	public ChannelTextInterfaceImp(Channel channel) {
		this.channel = channel;
		this.textGrip = channel.getRaw().getInterface(ChatChannelTextInterface.class);
	}

	@Override public Connection getConnection() {
		return channel.getConnection();
	}

	@Override public Client getClient() {
		return channel.getClient();
	}

	@Override public Mono<Message> send(String text) {
		return textGrip.send(text).map(message->new Message(channel.getConnection(), message));
	}

	@Override public Mono<Message> send(Consumer<MessageCreateSpec> spec) {
		return textGrip.send(spec).map(message->new Message(channel.getConnection(), message));
	}
}