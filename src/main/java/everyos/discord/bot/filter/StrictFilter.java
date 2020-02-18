package everyos.discord.bot.filter;

public class StrictFilter implements Filter {
	public static StrictFilter filter;

	@Override public String filter(String text) {
		return text.replace("@", "@ ");
	}
	
	static {
		filter = new StrictFilter();
	}
}
