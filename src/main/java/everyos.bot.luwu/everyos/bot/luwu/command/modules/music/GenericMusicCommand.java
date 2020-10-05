package everyos.bot.luwu.command.modules.music;

import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.command.modules.music.MusicAdapter.VoiceStateMissingException;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Message;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public abstract class GenericMusicCommand implements Command {
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		Channel channel = data.getChannel();
		Message message = data.getMessage();
		return message.suppressEmbeds(true)
			.onErrorResume(e->Mono.empty())
			.then(message.getAuthorAsMember())
        	.flatMap(m->MusicAdapter.getFromMember(data.getBotInstance(), m))
        	.flatMap(ma->execute(message, data, argument, ma, channel))
        	.cast(Object.class)
        	.onErrorResume(e->{
        		if (e instanceof VoiceStateMissingException) {
        			return channel.createMessage(data.getLocale().localize("command.music.notinchannel"));
        		}
        		return Mono.error(e);
        	});
		//TODO: We should scan out VoiceStates in other guilds if the current guild does not have a voice state
	}

	abstract Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel);

	public boolean requiresDJ() {return true;}
}
