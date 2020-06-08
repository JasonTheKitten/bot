package everyos.discord.luwu.filter;

public class EveryoneFilter implements Filter {
	public static EveryoneFilter filter;

	@Override public String filter(String text) {
		return text.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
	}
	
	static {
		filter = new EveryoneFilter();
	}
}
