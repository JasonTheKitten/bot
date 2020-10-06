package everyos.bot.luwu.command.modules.music;

import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.entity.Message;
import everyos.bot.luwu.exception.TextException;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public abstract class GenericMusicCommand implements Command {
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		Channel channel = data.getChannel();
		Message message = data.getMessage();
		Member invoker = data.getInvoker();
		Locale locale = data.getLocale();
		
		//Supress embeds
		//Check if the member has/needs Luwu Music permissions
		//  If not, error
		//Get the cached voice connection for the channel/server
		//  If it does not exist, create it
		//  If it is for a different channel, that's an error
		//Execute the command with the connection
		
		return
			suppressEmbeds(message)
			.then(checkPerms(invoker, locale))
			.then(createConnection(invoker, locale))
			.flatMap(manager->execute(data, parser, manager));
    	/*.onErrorResume(e->{
    		if (e instanceof VoiceStateMissingException) {
    			return channel.createMessage(data.getLocale().localize("command.music.notinchannel"));
    		}
    		return Mono.error(e);
    	});*/
		//TODO: We should scan out VoiceStates in other guilds if the current guild does not have a voice state
	}

	private Mono<MusicManager> createConnection(Member invoker, Locale locale) {
		return null;
	}

	private Mono<Void> suppressEmbeds(Message message) {
		return message.suppressEmbeds(true)
			.onErrorResume(e->Mono.empty());
	}
	private Mono<Void> checkPerms(Member invoker, Locale locale) {
		//TODO: Connect perms, too
		return invoker.isDJ().flatMap(isDJ->{
			if (requiresDJ()&&!isDJ) {
				return Mono.error(new TextException(locale.localize("command.voice.error.perms")));
			}
			return Mono.empty();
		});
	}

	abstract Mono<?> execute(CommandData data, ArgumentParser parser, MusicManager manager);
	abstract boolean requiresDJ();
}
