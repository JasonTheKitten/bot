package everyos.bot.chat4j.functionality.message;

public interface MessageCreateSpec {
	public void setContent(String content);
	void addAttachment(String name, String imageURL);
}
