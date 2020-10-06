package everyos.bot.luwu.command.modules.music;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandContainer;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.command.modules.music.playlist.MusicPlaylistCommand;
import everyos.bot.luwu.exception.TextException;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class MusicCommand implements Command {
	private CommandContainer commands;

	public MusicCommand() {
		this.commands = new CommandContainer();

        //Commands
        Command musicPlayCommand = new MusicPlayCommand();
        Command musicStopCommand = new MusicStopCommand();
        Command musicSkipCommand = new MusicSkipCommand();
        Command musicShuffleCommand = new MusicShuffleCommand();
        Command musicPauseCommand = new MusicPauseCommand(true);
        Command musicUnpauseCommand = new MusicPauseCommand(false);
        Command musicNpCommand = new MusicNowPlayingCommand();
        Command musicRepeatCommand = new MusicRepeatCommand();
        Command musicQueueCommand = new MusicQueueCommand();
        Command musicPlaylistCommand = new MusicPlaylistCommand();
        Command musicRadioCommand = new MusicRadioCommand();
        Command musicVolumeCommand = new MusicVolumeCommand();
        Command musicRestartCommand = new MusicRestartCommand();

        commands.registerCommand("command.music.play", musicPlayCommand);
        commands.registerCommand("command.music.play.alias", musicPlayCommand);
        commands.registerCommand("stop", musicStopCommand);
        commands.registerCommand("skip", musicSkipCommand);
        commands.registerCommand("shuffle", musicShuffleCommand);
        commands.registerCommand("pause", musicPauseCommand);
        commands.registerCommand("unpause", musicUnpauseCommand);
        commands.registerCommand("np", musicNpCommand);
        commands.registerCommand("nowplaying", musicNpCommand);
        commands.registerCommand("repeat", musicRepeatCommand);
        commands.registerCommand("queue", musicQueueCommand);
        commands.registerCommand("playlist", musicPlaylistCommand);
        commands.registerCommand("radio", musicRadioCommand);
        commands.registerCommand("volume", musicVolumeCommand);
        commands.registerCommand("restart", musicRestartCommand);
	}
	
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		if (parser.isEmpty()) {
        	return data.getChannel().getInterface(ChatChannelTextInterface.class)
        		.send(data.getLocale().localize("command.error.missingsubcommand"));
		}
		
		String cmd = parser.eat();
        
		Command command = commands.getCommand(cmd, data.getLocale()); //TODO: Detect preferred locale
        
        if (command==null) return Mono.error(new TextException(data.getLocale().localize("command.error.invalidsubcommand", "command", cmd)));
	    
        return command.execute(data, parser);
	}
}
