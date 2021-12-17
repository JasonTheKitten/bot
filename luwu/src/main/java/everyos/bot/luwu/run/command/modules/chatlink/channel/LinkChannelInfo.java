package everyos.bot.luwu.run.command.modules.chatlink.channel;

import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.run.command.modules.chatlink.link.Link;
import reactor.core.publisher.Mono;

public interface LinkChannelInfo {
	
	long getLinkID();
	boolean isVerified();
	boolean isOpted();
	boolean isUserMuted(UserID senderID);
	
	// Evil convenience method
	Mono<Link> getLink();
	
}
