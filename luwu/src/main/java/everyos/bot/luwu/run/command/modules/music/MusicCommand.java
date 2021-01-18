package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;
import everyos.bot.luwu.run.command.modules.music.playlist.MusicPlaylistCommand;
import everyos.bot.luwu.run.command.modules.music.volume.MusicVolumeCommand;

public class MusicCommand extends MultiCommand {
	private CommandContainer commands;

	public MusicCommand() {
		super("command.music");
		
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
        Command musicRequeueCommand = new MusicRequeueCommand();
        Command musicQueueCommand = new MusicQueueCommand();
        Command musicPlaylistCommand = new MusicPlaylistCommand();
        Command musicRadioCommand = new MusicRadioCommand();
        Command musicVolumeCommand = new MusicVolumeCommand();
        Command musicRestartCommand = new MusicRestartCommand();

        commands.registerCommand("command.music.play", musicPlayCommand);
        commands.registerCommand("command.music.play.alias", musicPlayCommand);
        commands.registerCommand("command.music.stop", musicStopCommand);
        commands.registerCommand("command.music.skip", musicSkipCommand);
        commands.registerCommand("command.music.shuffle", musicShuffleCommand);
        commands.registerCommand("command.music.pause", musicPauseCommand);
        commands.registerCommand("command.music.unpause", musicUnpauseCommand);
        commands.registerCommand("command.music.nowplaying", musicNpCommand);
        commands.registerCommand("command.music.nowplaying.alias", musicNpCommand);
        commands.registerCommand("command.music.repeat", musicRepeatCommand);
        commands.registerCommand("command.music.requeue", musicRequeueCommand);
        commands.registerCommand("command.music.queue", musicQueueCommand);
        commands.registerCommand("command.music.playlist", musicPlaylistCommand);
        commands.registerCommand("command.music.radio", musicRadioCommand);
        commands.registerCommand("command.music.volume", musicVolumeCommand);
        commands.registerCommand("command.music.restart", musicRestartCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return this.commands;
	}
}
