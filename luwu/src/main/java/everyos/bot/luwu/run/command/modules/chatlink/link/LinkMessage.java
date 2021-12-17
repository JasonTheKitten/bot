package everyos.bot.luwu.run.command.modules.chatlink.link;

import java.util.Optional;

import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.user.LinkUser;

public interface LinkMessage {

	LinkUser getSender();
	LinkChannel getOriginChannel();
	Optional<String> getContent();
	LinkAttachment[] getAttachments();
	
}
