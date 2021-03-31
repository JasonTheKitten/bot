package everyos.bot.luwu.run.command.modules.configuration.prefix;

public interface PrefixServerEditSpec {
	void addPrefix(String prefix);
	void removePrefix(String prefix);
	void resetPrefix();
	PrefixServerInfo getInfo();
}
