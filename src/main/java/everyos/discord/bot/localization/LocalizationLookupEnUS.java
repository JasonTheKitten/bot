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
				return "302 Found";
			case BanSuccess:
				return "410 Gone";
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
			case AutoDelete:
				return "This message will automatically be deleted";
			case MessagesPurged:
				return "Messages purged!";
			case MusicPaused:
				return "Music paused!";
			case MusicStopped:
				return "The queue has been cleared, and the music stopped. Bye!";
			case MusicUnpaused:
				return "Unpaused your music!";
			case NoGiphyKey:
				return "Missing necessary configurations: Please contact the bot owner.";
			case NoTrackPlaying:
				return "Nothing seems to be playing. Why don't you start some music!?";
			case ProfileCommandExtendedHelp:
				break;
			case ProfileCommandHelp:
				break;
			case QueueShuffled:
				return "The queue has been shuffled.";
			case TrackSkipped:
				return "At your command";
			case Undocumented:
				return " Undocumented";
			case GiveawayPrompt:
				return "**Hosted by:** ${host}\n"+
					"**Ends at:** ${end}\n"+
					"**Winners:** ${winners}\n"+
					"**Prize:** ${prize}";
			case MusicRepeatSet:
				return "The music is now set to repeat";
			case MusicRepeatUnset:
				return "The music is no longer set to repeat";
			case MustBeInServer:
				return "Sorry, but this command only works in servers!";
			case TicketCreated:
				return "The ticket has been created!";
			case WelcomeMessageSet:
				return "The welcome message has been set!";
			case DefaultTicketMessage:
				return "Ticket opened by ${user.mention}. Our staff will be with you shortly.";
			case TicketMessageSet:
				return "The message for future tickets has been set.";
			case GiveawayTitle:
				return "Giveaway!";
			case NoSuchRole:
				return "No such role exists";
			case RoleAlreadyExists:
				return "That role already exists!";
			case RoleCreated:
				return "The role has been created";
			case RoleGiven:
				return "You have been given the role!";
			case Donate:
				return "We are very grateful for donations. You can donate to our paypal at ${url}. Thanks!";
			case RoleRemoved:
				return "Removed the role!";
			case SupportServer:
				return "My support server is at <${url}>!";
			case ConfigurationReset:
				return "The configuration has been reset!";
			case DBLVote:
				return "You can vote for --- on DBL at <${url}>!";
			case LeaveMessageSet:
				return "The leave message has been set!";
			case RoleAdded:
				return "The role has been added!";
			case TooManyRoles:
				return "Too many roles already exist here - try removing one";
        }
        return "This text should not appear";
	}
}