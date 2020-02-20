package everyos.discord.bot.localization;

import java.util.HashMap;

public class LocalizationProvider {
	public static HashMap<Localization, ILocalizationLookup> locales;
	static {
		locales = new HashMap<Localization, ILocalizationLookup>();
		locales.put(Localization.en_US, new LocalizationLookupEnUS());	
	}
	public static String localize(Localization locale, LocalizedString label) {
		ILocalizationLookup lookup = locales.get(locale);
		if (lookup == null) return "???";
		return lookup.lookup(label);
	}
	public static Localization of(String localestr) {
		switch(localestr) {
			case "en_US":
				return Localization.en_US;
			default:
				return null;
		}
	}
}
