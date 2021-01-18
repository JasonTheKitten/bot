package everyos.bot.luwu.core.entity;

public interface Locale {
	String localize(String name, String... args);
	boolean canLocalize(String name);
}
