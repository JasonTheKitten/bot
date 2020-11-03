package everyos.bot.luwu.language;

import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.bot.luwu.core.entity.Locale;

public class ResourceLocale implements Locale {
	private JsonObject json;

	public ResourceLocale(String resource) {
		this.json = JsonParser.parseReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(resource))).getAsJsonObject();
	}
	
	@Override public String localize(String name, String... args) {
		if (json.has(name)) {
			name = json.get(name).getAsString();
		}
		
		StringBuilder b = new StringBuilder(name);
		for (int i=0; i<args.length; i+=2) {
			b.append(","+args[i]+":"+args[i+1]);
		}
		return b.toString();
	}
}
