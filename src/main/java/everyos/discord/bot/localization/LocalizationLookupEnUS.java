package everyos.discord.bot.localization;

public class LocalizationLookupEnUS implements ILocalizationLookup {
	@Override public String lookup(LocalizedString label) {
		switch (label) {
			case HelpCredits:
				return "--- is written by EveryOS under the MIT License";
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
                return "${target} current balance is ${feth} feth, uwu";
            case ZeroBalance:
                return "${target} wallet is empty, --- sad ):";
            case ReceivedDaily:
                return "You got ${feth} feth uwu";
            case NoDaily:
                return "It's not quite time, --- sad ):\n"+
                    "Just another ${h} hours, ${m} minutes, and ${s} seconds though!";
            case UnrecognizedUsage:
                return "I can't quite comprehend that command usage!";
            case UnrecognizedUser:
                return "--- sad because --- can't find that user!";
            case NotEnoughCurrency:
                return "--- sad because you don't have that much currency.";
            case MoneySent:
                return "Sent the money to the user, uwu";
            case KickFail:
				return "--- had troubles kicking the users";
			case BanFail:
				return "--- had troubles banning the users";
			case KickSuccess:
			case BanSuccess:
				return "The users - gone";
			case InsufficientPermissions:
				return "No";
			case Uptime:
				return "Total uptime is ${h} hours, ${m} minutes, and ${s} seconds\n"+
					"Connection uptime is ${ch} hours, ${cm} minutes, and ${cs} seconds";
			case ChannelAlreadyInUse:
				return "Warning: This channel is already in use";
			case RatedImage:
				return "Posted an innapropriate image";
			case Ping:
				return "Ping!\n2-way bot latency is ${ping}ms";
			case Info:
                return "Info";
            case InfoDescription:
                return "--- is a Discord bot written by EveryOS in the year of 2020.\n"+
                    "**Support server:** ${support_server}\n"+
                    "**GitHub repo:** ${gh_repo}\n"+
                    "**DBL Page:** ${dbl_page}\n"+
                    "Be sure to star the bot on GitHub and vote for it on DBL!";
			case ChatLinkOOBE:
                return "Chatlink created.\n"+
                    "To connect from another channel or server, please run `<@${ping}> link join ${id}`\n"+
                    "To accept an incoming connection, run the command from joining a link\n"+
                    "To locally mute a user, use search history to look up their ID, then run `<@${ping}> link mute <id>`\n"+
                    "To globally mute a user, use search history to look up their ID, then run `<@${ping}> link gmute <id>`";
			case UnfinishedChatLinkConnection:
                return "Please accept the chat link connection as a chat link admin!";
            case UnrecognizedChatLink:
                return "--- doesn't recognize that chat link group, so --- sad!";
            case AcceptChatLinkPrompt:
                return "Please accept this chatlink by having a chatlink administrator run `link accept ${id}` in the other server.";
            case ChannelNotAwaitingChatlink:
                return "Please join the chatlink from that channel using the pinned message!";
            case ChatLinkAccepted:
                return "The chat link has been accepted, ---!";
            case ChannelsSet:
                return "The channels have been set, ---";
            case UnrecognizedChannel:
                return "--- does not recognize that channel, so --- sad!";
            case SuggestionBy:
                return "Suggestion by ${user}";
            case SuggestionFooter:
                return "User ID: ${id}\nUse the reactions to vote!";
            case OnPing:
                return "--- at your service!";
            case CurrencyStealing:
                return "That would be stealing!";
        }
        return "???";
	}
}