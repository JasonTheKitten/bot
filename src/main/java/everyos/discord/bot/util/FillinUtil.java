package everyos.discord.bot.util;

import java.util.HashMap;

public class FillinUtil {
	public static HashMap<String, String> of(String... args) {
		HashMap<String, String> fillins = new HashMap<String, String>();
		for (int i=0; i<args.length; i+=2) fillins.put(args[i], args[i+1]);
		return fillins;
	}
}