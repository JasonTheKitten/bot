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
				return "**[user]** The user to view. The default is yourself";
			case ProfileCommandHelp:
				return "[user] Displays a variety of info about a user";
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
			case ActionCancelled:
				return "The ongoing operation has been cancelled!;";
			case StarboardSet:
				return "Created the starboard!";
			case AdventureCommandExtendedHelp:
				break;
			case AdventureCommandHelp:
				break;
			case AkinatorCommandExtendedHelp:
				break;
			case AkinatorCommandHelp:
				break;
			case AnnounceCommandExtendedHelp:
				return "**[text]** text not to process;";
			case AnnounceCommandHelp:
				return "[text] Ignores the text";
			case AutoRoleCommandExtendedHelp:
				break;
			case AutoRoleCommandHelp:
				break;
			case AutomodCommandExtendedHelp:
				break;
			case AutomodCommandHelp:
				break;
			case BanCommandExtendedHelp:
				return "**<user>** The user to be banned";
			case BanCommandHelp:
				return "<user> Bans the specified user";
			case CancelCommandExtendedHelp:
				break;
			case CancelCommandHelp:
				break;
			case CurrencyCommandExtendedHelp:
				break;
			case CurrencyCommandHelp:
				break;
			case DBLVoteCommandExtendedHelp:
				break;
			case DBLVoteCommandHelp:
				break;
			case DictionaryCommandExtendedHelp:
				break;
			case DictionaryCommandHelp:
				break;
			case DisableCommandExtendedHelp:
				break;
			case DisableCommandHelp:
				break;
			case DonateCommandExtendedHelp:
				break;
			case DonateCommandHelp:
				break;
			case FilterCommandExtendedHelp:
				break;
			case FilterCommandHelp:
				break;
			case GiphyCommandExtendedHelp:
				return "**[query]** The type of image to search. Defaults to *cat*";
			case GiphyCommandHelp:
				return "[query] Search an image via giphy";
			case GiveawayCommandExtendedHelp:
				break;
			case GiveawayCommandHelp:
				break;
			case HelpCommandExtendedHelp:
				return "**[command/group]** The command and/or group to show help for. Defaults to primary commands for the current channel.";
			case HelpCommandHelp:
				return "[command/group] Displays the help screen";
			case HugCommandExtendedHelp:
				return "**[user]** The user to hug. Default is yourself";
			case HugCommandHelp:
				return "[user] Hug a user!";
			case IgnoreCommandExtendedHelp:
				break;
			case IgnoreCommandHelp:
				break;
			case InfoCommandExtendedHelp:
				return "Shows GH url, dbl url, etc";
			case InfoCommandHelp:
				return "Displays a variety of neat little info blurbs";
			case KickCommandExtendedHelp:
				return "**<user>** The user to kick";
			case KickCommandHelp:
				return "<user> Kicks the specified user";
			case LMSGCommandExtendedHelp:
				return "**<channel>** The channel to send messages to\n" +
					"**[message]** The message to be sent upon user leave";
			case LMSGCommandHelp:
				return "<channel> [message] Set a message to be sent when users leave";
			case LevelCommandExtendedHelp:
				break;
			case LevelCommandHelp:
				break;
			case MessageCommandExtendedHelp:
				break;
			case MessageCommandHelp:
				break;
			case MusicCommandExtendedHelp:
				break;
			case MusicCommandHelp:
				break;
			case MuteCommandExtendedHelp:
				break;
			case MuteCommandHelp:
				break;
			case OWMCommandExtendedHelp:
				break;
			case OWMCommandHelp:
				break;
			case OneWordCommandExtendedHelp:
				return "No additional usage details";
			case OneWordCommandHelp:
				return "Creates a new one-word game channel";
			case PingCommandExtendedHelp:
				return "Returns the two-way latency in millis";
			case PingCommandHelp:
				return "Check the bot's ping time";
			case PurgeCommandExtendedHelp:
				return
					"**<messages>** Can be 'all', 'after [mid]', or an amount. Deletes a number of messages\n"+
					"**[arguments...] Can be a user id to specify users whose messages should be deleted (default is all users) "+
						"or a channel id to specify channels to include in the purge (default is current channel only)\n"+
					"This command can only purge up to two weeks of messages";
			case PurgeCommandHelp:
				return "<messages> [arguments...] Deletes multiple messages at once";
			case ReactionCommandExtendedHelp:
				break;
			case ReactionCommandHelp:
				break;
			case RemindCommandExtendedHelp:
				break;
			case RemindCommandHelp:
				break;
			case RemindIntervalCommandExtendedHelp:
				break;
			case RemindIntervalCommandHelp:
				break;
			case RoleCommandExtendedHelp:
				break;
			case RoleCommandHelp:
				break;
			case SuggestCommandExtendedHelp:
				return "**<suggestion>** The content of the suggestion";
			case SuggestCommandHelp:
				return "<suggestion> Create a new suggestion with voting reactions in the current channel";
			case SupportCommandExtendedHelp:
				break;
			case SupportCommandHelp:
				break;
			case TicketCommandExtendedHelp:
				return "No additional usage";
			case TicketCommandHelp:
				return "Creates a new ticket";
			case TicketManagerCommandExtendedHelp:
				break;
			case TicketManagerCommandHelp:
				break;
			case TranslateCommandExtendedHelp:
				break;
			case TranslateCommandHelp:
				break;
			case UnmuteCommandExtendedHelp:
				break;
			case UnmuteCommandHelp:
				break;
			case UptimeCommandExtendedHelp:
				return "Total uptime is time since the bot was initially booted\nConnection uptime is time connected to gateway";
			case UptimeCommandHelp:
				return "Displays the bot's uptime";
			case WMSGCommandExtendedHelp:
				return "**<channel>** The channel to send messages to\n" +
					"**[message]** The message to be sent upon user join";
			case WMSGCommandHelp:
				return "<channel> [message] Set a message to be sent when users join";
			case StarboardCommandExtendedHelp:
				break;
			case StarboardCommandHelp:
				break;
			case SuggestionsCommandExtendedHelp:
				break;
			case SuggestionsCommandHelp:
				break;
			case TicketAlreadyExists:
				break;
        }
        return "This text should not appear";
	}
}