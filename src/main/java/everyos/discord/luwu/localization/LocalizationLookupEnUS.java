package everyos.discord.luwu.localization;

public class LocalizationLookupEnUS implements ILocalizationLookup {
	@Override public String lookup(LocalizedString label) {
		switch (label) {
			case HelpCredits:
				return "Luwu is written by EveryOS under the MIT License";
			case HelpTitle:
                return "Help (Click to go to repository)";
            case NoSuchCommand:
                return "No such command!";
            case NoSuchSubcommand:
                return "No such subcommand! (The primary command is valid)";
            case NoSuchGroup:
                return "No such command group!";
            case ExtendedHelp:
                return "${command} - Extended Help";
            case CommandHelp:
                return "help";
            case CurrentBalance:
                return "${target} current balance is ${feth} feth, uwu";
            case ZeroBalance:
                return "${target} wallet is empty, Luwu sad ):";
            case ReceivedDaily:
                return "You got ${feth} feth uwu";
            case NoDaily:
                return "It's not quite time, Luwu sad ):\n"+
                    "Just another ${h} hours, ${m} minutes, and ${s} seconds though!";
            case UnrecognizedUsage:
                return "I can't quite comprehend that command usage!";
            case UnrecognizedUser:
                return "Luwu sad because Luwu can't find that user!";
            case NotEnoughCurrency:
                return "Luwu sad because you don't have that much currency.";
            case MoneySent:
                return "Sent the money to the user, uwu";
            case KickFail:
				return "Luwu had troubles kicking the users";
			case BanFail:
				return "Luwu had troubles banning the users";
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
                return "Luwu is a Discord bot written by EveryOS in the year of 2020.\n"+
                    "**Support server:** ${support_server}\n"+
                    "**GitHub repo:** ${gh_repo}\n"+
                    "**DBL Page:** ${dbl_page}\n"+
                    "**Invite:** ${invite_url}\n"+
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
                return "Luwu doesn't recognize that chat link group, so Luwu sad!";
            case AcceptChatLinkPrompt:
                return "Please accept this chatlink by having a chatlink administrator run `link accept ${id}` in the other server.";
            case ChannelNotAwaitingChatlink:
                return "Please join the chatlink from that channel using the pinned message!";
            case ChatLinkAccepted:
                return "The chat link has been accepted, Luwu!";
            case ChannelsSet:
                return "The channels have been set, Luwu";
            case UnrecognizedChannel:
                return "Luwu does not recognize that channel, so Luwu sad!";
            case SuggestionBy:
                return "Suggestion by ${user}";
            case SuggestionFooter:
                return "User ID: ${id}\nUse the reactions to vote!";
            case OnPing:
                return "Luwu at your service!";
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
				return "You can vote for Luwu on DBL at <${url}>!";
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
				return "Escape the current prompt";
			case CurrencyCommandExtendedHelp:
				break;
			case CurrencyCommandHelp:
				return "[subcommand] Yet another currency system";
			case DBLVoteCommandExtendedHelp:
				break;
			case DBLVoteCommandHelp:
				break;
			case DictionaryCommandExtendedHelp:
				return "**<word>** The word to check the definition of";
			case DictionaryCommandHelp:
				return "<word> Check the definition of a word";
			case DisableCommandExtendedHelp:
				break;
			case DisableCommandHelp:
				break;
			case DonateCommandExtendedHelp:
				return ":/";
			case DonateCommandHelp:
				return "Get a link to donate";
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
				return "<user> The user to be ignored";
			case IgnoreCommandHelp:
				return "**<user>** Causes the bot to ignore the specified user's commands";
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
				return "**[user]** The user whose level is to be displayed. Defaults to the invoker";
			case LevelCommandHelp:
				return "[user] Displays a user's level";
			case MessageCommandExtendedHelp:
				break;
			case MessageCommandHelp:
				break;
			case MusicCommandExtendedHelp:
				break;
			case MusicCommandHelp:
				return "[subcommand] Play music and manage playlists";
			case MuteCommandExtendedHelp:
				return "<user> The user to mute";
			case MuteCommandHelp:
				return "**<user>** Mute a user. (Beta, locks channel to user *after* message send)";
			case OWMCommandExtendedHelp:
				break;
			case OWMCommandHelp:
				return "One word moderation module. Use help to view subcommands";
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
				return "<messages> [arguments...] Deletes multiple messages at once (Slow to respond in large channels)s";
			case ReactionCommandExtendedHelp:
				break;
			case ReactionCommandHelp:
				return "The reaction roles module. Use help to view subcommands.";
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
				return "This module allows you to manage self-assignable roles (e.g. color roles). Use help to view subcommands.";
			case SuggestCommandExtendedHelp:
				return "**<suggestion>** The content of the suggestion";
			case SuggestCommandHelp:
				return "<suggestion> Create a new suggestion with voting reactions in the current channel";
			case SupportCommandExtendedHelp:
				return "No additional usage";
			case SupportCommandHelp:
				return "Gives a link to this bot's support server";
			case TicketCommandExtendedHelp:
				return "No additional usage";
			case TicketCommandHelp:
				return "Creates a new ticket";
			case TicketManagerCommandExtendedHelp:
				break;
			case TicketManagerCommandHelp:
				break;
			case TranslateCommandExtendedHelp:
				return "**[\\*lang]** The language to translate the text into. Can be *english";
			case TranslateCommandHelp:
				return "[\\*lang] Translate a phrase to another language!";
			case UnmuteCommandExtendedHelp:
				break;
			case UnmuteCommandHelp:
				return "**<user>** Mutes a user. This is an alpha implementation that doesn't lock the channels"+
				" until a muted user sends a message, so it is advised that you manually lock the channels for now";
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
				return "The starboard module. Use help to view subcommands.";
			case SuggestionsCommandExtendedHelp:
				return "**[channel]** A channel to use for suggestions. Defaults to current channel.";
			case SuggestionsCommandHelp:
				return "[channel] Sets a dedicated suggestions channel";
			case TicketAlreadyExists:
				return "You already have an open ticket!";
			case MusicRadioSet:
				return "The radio is now turned on";
			case MusicRadioUnset:
				return "The radio is now turned off";
			case UnrecognizedWord:
				return "This word is not recognized";
			case NotInMusicChannel:
				return "Please join a music channel in this server to use this command";
			case InsufficientBotPermissions:
				return "Luwu has insufficient permissions and requires the follow permission: ${permission}";
			case MemberMuted:
				return "The member has been muted successfully!";
			case MemberUnmuted:
				return "The member has been unmuted successfully!";
			case CommandAlias:
				return "An alias for '${command}'";
			case HugSent:
				return "You've been sent a hug!";
			case MustBeInVoiceChannel:
				return "You must be in a voice channel to use this command!";
			case SelfSendMoney:
				return "You can't send yourself money!";
			case SendMoneyThankYou:
				return "Thank you, uwu";
			case HugSentBot:
				return "Luwu wuvs you!";
			case HugSentUser:
				return "${invoker} sent ${recipient} a hug!";
			case BanUserReason:
				return "User bad invocated by user";
			case KickUserReason:
				return "User kick invocated by user";
			case NotLinkAdmin:
				return "This channel is not in the chatlink administration group";
			case OneWordLastUser:
				return "The last user was ${ping}";
			case OneWordNotRecognized:
				return "I don't quite recognize that word!";
			case OneWordOneWord:
				return "You can only say one word at a time!";
			case OneWordSentenceReset:
				return "The sentence has been reset";
			case HelpCommandsFooter:
				return "For additional command usage, run `help [command]` (e.g. `help music playlist create`)\nWritten by EveryOS";
			case HelpGroupsFooter:
				return "Don't forget the > sign!\nWritten by EveryOS";
			case MemberIgnored:
				return "The specified user will now be ignored";
			case MemberMustBeHigher:
				return "You must be higher than the specified member to run this command!";
			case NoPrefixExists:
				return "That prefix does not exist!";
			case OnePrefixMin:
				return "At least one prefix must be kept available";
			case PrefixAdded:
				return "Added the prefix";
			case PrefixAlreadyExists:
				return "That prefix already exists!";
			case PrefixesReset:
				return "The prefixes have been reset";
			case PrefixEmpty:
				return "This prefix cannot be used because it renders as empty!";
			case PrefixTooLong:
				return "This prefix is too long!";
			case NoStarsLeft:
				return "No stars remain configured. Default configurations will be applied until new configurations are set.";
			case NoStarMatch:
				return "No such star exists";
			case NotFoundException:
				return "The command was unable to complete because an entity was not found";
			case StarAlreadyExistsForAmount:
				return "A star was already set for this amount";
			case StarRemoved:
				return "Removed the star type!";
			case StarTypeAdd:
				return "Added the star type!";
			case StarboardIgnoredChannel:
				return "This channel will now be ignored on the starboard";
			case StarboardUnignoredChannel:
				return "This channel will no longer be ignored on the starboard";
			case GhostCommandExtendedHelp:
				return "[...] The message to ghost";
			case GhostCommandHelp:
				return "[...] Send a \"ghost\" message (still gets logged to mod logs, if configured), useful for announcements. "+
					"The invoker must have the MANAGE_ROLES permission";
			case ModLogChannelCleared:
				return "Mod log is now turned off";
			case ModLogChannelSet:
				return "The mod log channel has been set";
			case CreatePlaylistFirst:
				return "You must create the playlist first!";
			case PlaylistAlreadyExists:
				return "This playlist already exists!";
			case PlaylistTrackAdded:
				return "The song has been added to the playlist!";
			case UseQuotesPlaylist:
				return "Please surround the name of the playlists with \"quotes\"";
			case PlaylistDeleted:
				return "The playlist has been deleted!";
			case PlaylistDoesNotExist:
				return "This playlist does not exist";
			case LogsChannelCleared:
				return "Logging is now turned off";
			case LogsChannelSet:
				return "Logging is now set";
			case PrefixPing:
				return "Player 1 Ready";
			case DataWiped:
				return "The requested data has been wiped";
			case MustForce:
				return "You must run this command with an argument that is a case-sensitive match to -f or --force";
			case PlaylistCreated:
				return "The playlist has been created!";
			case PlaylistQueued:
				return "All songs in the playlist ${name} have been added to queue!";
			case PlaylistTrackRemoved:
				return "The track has been removed from your playlist!";
			case PlaylistTooManySongs:
				return "Too many songs are already in your playlist. Please remove one first";
			case CurrentVolume:
				return "The current volume is ${volume}!";
			case VolumeSet:
				return "The new volume is ${volume}! (May take a few seconds to apply)";
			case VolumeTooLow:
				return "This volume is too low!";
			case LinkChannelOpted:
				return "The chatlink channel is now opted";
			case LinkUserGloballyMuted:
				return "This user is now globally muted";
			case LinkUserGloballyUnmuted:
				return "This user is no longer globally muted";
			case UserAlreadyGloballyMuted:
				return "This user is already globaly muted!";
			case UserNotGloballyMuted:
				return "This user was not globally muted in the first place.";
			case NotLinkChannel:
				return "This channel is not in the chatlink!";
			case MusicRestarted:
				return "The current track has been restarted!";
			case BattleTitle:
				return "**Battle!**";
			case BattleYourTurn:
				return "It's your turn!";
			case CurrencyBalanceCommandExtendedHelp:
				return "[user] The user to view the balance of. Defaults to yourself";
			case CurrencyBalanceCommandHelp:
				return "**[user]** View the balance of the selected user";
			case CurrencyDailyCommandExtendedHelp:
				return "No additional usage";
			case CurrencyDailyCommandHelp:
				return "Gives you a daily feth reward";
			case CurrencyGiveCommandExtendedHelp:
				return "<user> The user to give feth to";
			case CurrencyGiveCommandHelp:
				return "**<user>** Transfers feth to the selected user";
			case CurrencyTopCommandExtendedHelp:
				return "No additional usage";
			case CurrencyTopCommandHelp:
				return "Displays the currency leaderboard";
			case MusicNowPlayingCommandExtendedHelp:
				return "No additional usage";
			case MusicNowPlayingCommandHelp:
				return "Indicates the currently playing song";
			case MusicPauseCommandExtendedHelp:
				return "No additional usage";
			case MusicPauseCommandHelp:
				return "Pauses the music";
			case MusicPlayCommandExtendedHelp:
				return "<song> The song, URL, or (coming soon) upload of the music to play";
			case MusicPlayCommandHelp:
				return "**<song>** Adds a song to the music queue";
			case MusicPlaylistAddCommandExtendedHelp:
				return "<playlist> The playlist to add a song to\n"
					+ "<song> the song to add to the playlist";
			case MusicPlaylistAddCommandHelp:
				return "**\"<playlist>\" <song>** Adds a song to the specified playlist";
			case MusicPlaylistCommandExtendedHelp:
				break;
			case MusicPlaylistCommandHelp:
				return "The playlist module. Use help to view subcommands";
			case MusicPlaylistCreateCommandExtendedHelp:
				return "<playlist> The name of the playlist to create";
			case MusicPlaylistCreateCommandHelp:
				return "**\"<playlist>\"** Creates a playlist";
			case MusicPlaylistDeleteCommandExtendedHelp:
				return "<playlist> The name of the playlist to delete";
			case MusicPlaylistDeleteCommandHelp:
				return "**\"<playlist>\"** Deletes a playlist";
			case MusicPlaylistPlayCommandExtendedHelp:
				return "<playlist> The name of the playlist to play";
			case MusicPlaylistPlayCommandHelp:
				return "**\"<playlist>\"** Plays a playlist";
			case MusicPlaylistQuickAddCommandExtendedHelp:
				return "<playlist> The name of the playlist to quick-add to";
			case MusicPlaylistQuickAddCommandHelp:
				return "**\"<playlist>\"** Adds the currently playing song to the specified playlist";
			case MusicPlaylistRemoveCommandExtendedHelp:
				return "Listen already, it's the `*music playlist remove` command";
			case MusicPlaylistRemoveCommandHelp:
				return "It's the playlist remove command, what else did you expect?";
			case MusicQueueCommandExtendedHelp:
				return "Shows the now playing view if only one song is queued";
			case MusicQueueCommandHelp:
				return "View which songs are in queue.";
			case MusicRadioCommandExtendedHelp:
				return "Inverts the current radio on/off state.\nThe queue takes priority over radio.";
			case MusicRadioCommandHelp:
				return "Turns the radio on or off";
			case MusicRepeatCommandExtendedHelp:
				return "Inverts the repeat state.\nRepeats the entire queue, not just the current song.";
			case MusicRepeatCommandHelp:
				return "Turns repeat mode on or off";
			case MusicRestartCommandExtendedHelp:
				return "No additional usage";
			case MusicRestartCommandHelp:
				return "Restarts the current playing song";
			case MusicShuffleCommandExtendedHelp:
				return "Performs a one-time shuffle, not a continuous shuffle";
			case MusicShuffleCommandHelp:
				return "Randomizes the order of the queue";
			case MusicSkipCommandExtendedHelp:
				return "No additional usage";
			case MusicSkipCommandHelp:
				return "Skips the current song and removes it from the repeat queue";
			case MusicStopCommandExtendedHelp:
				return "No additional usage";
			case MusicStopCommandHelp:
				return "Stops the music, clears the queue, and turns off radio and repeat modes";
			case MusicUnpauseCommandExtendedHelp:
				return "No additional usage";
			case MusicUnpauseCommandHelp:
				return "Unpauses the music";
			case MusicVolumeCheckCommandExtendedHelp:
				return "No additional usage";
			case MusicVolumeCheckCommandHelp:
				return "Displays the current volume that the music is set to";
			case MusicVolumeCommandExtendedHelp:
				break;
			case MusicVolumeCommandHelp:
				return "The music volume module. Use help to view subcommands";
			case MusicVolumeDownCommandExtendedHelp:
				return "<amount> The amount to turn the volume down by";
			case MusicVolumeDownCommandHelp:
				return "**<amount>** Turn down the music by a specified amount";
			case MusicVolumeSetCommandExtendedHelp:
				return "<volume> The new volume";
			case MusicVolumeSetCommandHelp:
				return "**<volume>** Set the volume that the music should play at";
			case MusicVolumeUpCommandExtendedHelp:
				return "<amount> The amount to turn the volume up by";
			case MusicVolumeUpCommandHelp:
				return "**<amount>** Turn up the music by a specified amount";
			case BattleCommandHelp:
				return "Ignore this command, it does nothing";
        }
        return "This text should not appear";
	}
}