package everyos.bot.chat4j.functionality.message;

import everyos.bot.chat4j.entity.ChatColor;

public interface EmbedSpec {
	public void setTitle(String string);
	public void setColor(ChatColor color);
	public void setDescription(String string);
	public void addField(String name, String content, boolean inline);
	public void setFooter(String footer);
	public void setImage(String burl);
	public void setAuthor(String author, String url, String image);
}
