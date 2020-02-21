package everyos.discord.bot.localization;

public class LocalizationLookupEnUS implements ILocalizationLookup {
	@Override public String lookup(LocalizedString label) {
		switch (label) {
			case HelpCredits:
				return "";
			case HelpTitle:
                return "Help (Click to go to repository)";
            case NoSuchCommand:
                return "No such command!";
            case NoSuchSubcommand:
                return "No such subcommand!";
            case NoSuchGroup:
                return "No such command group!";
            case ExtendedHelp:
                return "${command} - Extended Help";
            case CommandHelp:
                return "help";
            case CurrentBalance:
                return "Your current balance is ${feth} feth, uwu";
            case ZeroBalance:
                return "Your wallet is empty, --- sad ):";
            case ReceivedDaily:
                return "You got ${feth} feth uwu";
            case NoDaily:
                return "It's not quite time, --- sad ):\n"+
                    "Just another ${h} hours, ${m} minutes, and ${s} seconds though!";
        }
        return "???";
	}
}
