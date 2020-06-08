package everyos.discord.luwu.filter;

public class StrictFilter implements Filter {
	public static StrictFilter filter;

	@Override public String filter(String text) {
		return text.replace("@", "@\u200B");
	}
	
	static {
		filter = new StrictFilter();
	}
}
