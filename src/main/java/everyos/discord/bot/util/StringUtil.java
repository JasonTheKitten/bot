package everyos.discord.bot.util;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class StringUtil {
	public static String sub(@Nonnull String str, int start) {
		return sub(str, start, str.length());
	}
	public static String sub(@Nonnull String str, int start, int end) {
		if (start<0) start = 0;
		if (start>str.length()) return "";
		if (end>str.length()) end = str.length();
		if (end<start) return "";
		return str.substring(start, end);
	}
	public static String[] split(@Nonnull String arg, @Nonnull String splitter) {
		if (arg.length()==0) return new String[0];
		
		ArrayList<String> args = new ArrayList<String>();
		StringBuilder str = null;
		for (int i=0; i<arg.length(); i++) {
			String ch = arg.substring(i, i+1);
			if (ch.equals(splitter)) {
				if (str!=null) {
					args.add(str.toString());
					str = null;
				}
			} else {
				if (str == null) {
					str = new StringBuilder();
				}
				str.append(ch);
			}
        }
		if (str!=null) args.add(str.toString());
		
		return args.toArray(new String[args.size()]);
    }
    public static String split1(@Nonnull String str, @Nonnull String spl) {
        str = str.replaceAll(spl+"+", spl);
        if (str.startsWith(spl)) str = sub(str, spl.length());
        if (str.indexOf(spl)==-1) return str;
        return sub(str, 0, str.indexOf(spl));
    }
    public static String split2(@Nonnull String str, @Nonnull String spl) {
        str = str.replaceAll(spl+"+", spl);
        if (str.startsWith(spl)) str = sub(str, spl.length());
        if (str.indexOf(spl)==-1) return "";
        return sub(str, str.indexOf(spl)+1);
    }
}