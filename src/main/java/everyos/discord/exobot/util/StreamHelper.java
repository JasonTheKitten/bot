package everyos.discord.exobot.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamHelper {
	public static String read(InputStream stream) throws IOException{
		StringBuilder content = new StringBuilder();
		
		int ch;
		while ((ch=stream.read())!=-1) {
			content.append((char) ch);
		}
		
		return content.toString();
	}
}
