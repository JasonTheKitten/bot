package everyos.bot.chat4j.entity;

public class ChatColor {
	private final int color;

	public ChatColor(int r, int g, int b, int a) {
		this.color = (r<<24)+(g<<16)+(b<<8)+a;
	}
	public ChatColor(int r, int g, int b) {
		this(r, g, b, 255);
	}
	
	public int getRed() {
		return color>>24&255;
	}
	public int getGreen() {
		return color>>16&255;
	}
	public int getBlue() {
		return color>>8&255;
	}
	public static ChatColor of(int r, int g, int b) {
		return new ChatColor(r, g, b);
	}
}
