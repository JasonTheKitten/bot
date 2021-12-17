package everyos.bot.chat4j.functionality.message;

import java.util.function.Consumer;

public interface MessageCreateSpec {
	void setContent(String content);
	void addAttachment(String name, String imageURL, boolean spoiler);
	void setPresanitizedContent(String content);
	void setEmbed(Consumer<EmbedSpec> spec); //TODO: Is it supported?
}
