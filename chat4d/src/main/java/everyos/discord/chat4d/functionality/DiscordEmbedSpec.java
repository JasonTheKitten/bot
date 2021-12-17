package everyos.discord.chat4d.functionality;

import java.time.Instant;

import discord4j.core.spec.legacy.LegacyEmbedCreateSpec;
import discord4j.rest.util.Color;
import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.chat4j.functionality.message.EmbedSpec;

public class DiscordEmbedSpec implements EmbedSpec {
	
	private LegacyEmbedCreateSpec spec = null;
	private String footer = null;

	public DiscordEmbedSpec(LegacyEmbedCreateSpec eSpec) {
		this.spec = eSpec;
		eSpec.setTimestamp(Instant.now());
		//TODO: Method to set a timestamp
	}

	@Override
	public void setTitle(String title) {
		spec.setTitle(title);
	}

	@Override
	public void setColor(ChatColor color) {
		spec.setColor(Color.of(color.getRed(), color.getGreen(), color.getBlue()));
	}
	
	@Override
	public void setDescription(String description) {
		spec.setDescription(description);
	}

	@Override
	public void addField(String name, String content, boolean inline) {
		if (name==null||name.isEmpty()) return;
		if (content==null||content.isEmpty()) return;
		spec.addField(name, content, inline);
	}

	@Override
	public void setFooter(String footer) {
		this.footer = footer;
		updateFooter();
	}

	@Override
	public void setImage(String url) {
		spec.setImage(url);
	}

	@Override
	public void setAuthor(String author, String url, String image) {
		spec.setAuthor(author, url, image);
	}
	
	private void updateFooter() {
		spec.setFooter(footer, null);
	}
	
}