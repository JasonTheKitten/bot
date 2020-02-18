package everyos.discord.bot.filter;

public class NoFilter implements Filter {
	public static NoFilter filter;

	public String filter(String text) {
		return text;
	}
	
	static {
		filter = new NoFilter();
	}
}
