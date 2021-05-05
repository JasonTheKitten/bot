package everyos.bot.luwu.core;

import java.util.Optional;

public interface Configuration {
	public Optional<String> getDiscordToken();
	public Optional<String> getNertiviaToken();
	public String getDatabaseURL();
	public String getDatabasePassword();
	public String getDatabaseName();
	@Deprecated
	public String getCustomField(String fieldName); //TODO: Just doing this for quick programming, should not exist
}
