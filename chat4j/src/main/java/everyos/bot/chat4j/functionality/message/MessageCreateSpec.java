package everyos.bot.chat4j.functionality.message;

public interface MessageCreateSpec {
	void setContent(String content);
	void addAttachment(String name, String imageURL);
	void setPresanitizedContent(String content);
}
