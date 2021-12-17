package everyos.bot.luwu.run.command.modules.chatlink.link;

import java.util.Optional;

public interface LinkInfo {
	
	Optional<String> getRules();
	boolean isAutoVerify();
	
}
