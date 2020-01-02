package everyos.discord.exobot.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorUtil {
	public static String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		return sw.toString();
	}
}
