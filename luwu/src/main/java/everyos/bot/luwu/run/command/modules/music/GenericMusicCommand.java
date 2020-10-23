package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.exception.TextException;
import reactor.core.publisher.Mono;

public abstract class GenericMusicCommand implements Command {
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
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
		//TODO: We should cache info about which users are in which VC
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

	abstract Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager);
	abstract boolean requiresDJ();
}
