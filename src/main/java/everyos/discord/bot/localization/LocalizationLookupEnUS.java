package everyos.discord.bot.localization;

public class LocalizationLookupEnUS implements ILocalizationLookup {
	@Override public String lookup(LocalizedString label) {
		switch (label) {
			case HelpCredits:
				return "";
			case HelpTitle:
				return "Help (Click to go to repository)";
			default:
				return "???";
		}
	}
}
