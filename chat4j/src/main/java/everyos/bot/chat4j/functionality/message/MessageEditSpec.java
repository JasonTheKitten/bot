package everyos.bot.chat4j.functionality.message;

import java.util.function.Consumer;

public interface MessageEditSpec {
	void setContent(String content);
	void setEmbed(Consumer<EmbedSpec> spec); //TODO: Is it supported?
}
