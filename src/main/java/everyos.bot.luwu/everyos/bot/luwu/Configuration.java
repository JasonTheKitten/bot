package everyos.bot.luwu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public interface Configuration {
	public String getDiscordToken();
	public String getNertiviaToken();
	public String getDatabaseURL();
	public String getDatabasePassword();
	public String getDatabaseName();
	
	public static Configuration loadFrom(File file) throws IOException {
		JsonObject config = JsonParser.parseReader(new InputStreamReader(new FileInputStream(file))).getAsJsonObject();
		
		return new Configuration() {
			@Override public String getDiscordToken() {
				return config.has("discord-token")?config.get("discord-token").getAsString():null;
			}

			@Override public String getNertiviaToken() {
				return config.has("nertivia-token")?config.get("nertivia-token").getAsString():null;
			}

			@Override public String getDatabaseURL() {
				return config.get("database-url").getAsString();
			}
			@Override public String getDatabasePassword() {
				return config.get("database-password").getAsString();
			}
			@Override public String getDatabaseName() {
				return config.get("database-name").getAsString();
			}
		};
	}
}
