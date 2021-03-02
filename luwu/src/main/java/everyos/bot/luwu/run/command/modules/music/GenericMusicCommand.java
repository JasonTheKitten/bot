package everyos.bot.luwu.run.command.modules.music;

import java.util.function.Function;

import everyos.bot.chat4j.audio.ChatVoiceStateMissingException;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelVoiceInterface;
import everyos.bot.luwu.core.functionality.member.MemberVoiceConnectionInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public abstract class GenericMusicCommand extends CommandBase {
	private static MusicCache cache = new MusicCache(); //TODO: Move this to the bot instance
	
	public GenericMusicCommand(String id, Function<Client, Boolean> checkSupportedFunc, int requiredBotPerms, int requiredUserPerms) {
		super(id, checkSupportedFunc, requiredBotPerms, requiredUserPerms);
	}
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
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
			.then(createConnection(invoker, locale))
			.flatMap(manager->execute(data, parser, manager))
	    	.onErrorResume(e->{
	    		if (e instanceof ChatVoiceStateMissingException) {
	    			return Mono.error(new TextException(locale.localize("command.music.notinchannel")));
	    		}
	    		return Mono.error(e);
	    	});
		//TODO: We should cache info about which users are in which VC
	}

	private Mono<MusicManager> createConnection(Member invoker, Locale locale) {
		//Get MusicManager of User
		//If does not exist, get MusicManager of Channel, and set User
		//If does not exist, create MusicManager for voicestate. Set User and Channel
		//If voicestate did not exist, error
		//If the MusicManager already existed, ensure that it is the same channel as the user was in.
		//If command requires connection, connect
		//If command does not require connection, and not connected, error
		
		return invoker.getServer().flatMap(server->{
			//TODO: Logic for if not connected or createsConnection
			Mono<MusicManager> createToCache = invoker.getInterface(MemberVoiceConnectionInterface.class).getVoiceState()
				.flatMap(voiceState->voiceState.getChannel())
				.flatMap(channel->{
					return cache.createToCache(server).flatMap(manager->{
						return channel.getInterface(ChannelVoiceInterface.class)
							.connect(manager.getBridge())
							.doOnNext(connection->manager.addCleanupListener(()->connection.leave().subscribe()))
							.then(Mono.just(manager));
					});
				});
			
			return cache.getFromCache(invoker)
				.switchIfEmpty(cache.getFromCache(server))
				.switchIfEmpty(createToCache);
		});
	}

	private Mono<Void> suppressEmbeds(Message message) {
		return message.suppressEmbeds(true)
			.onErrorResume(e->Mono.empty());
	}
	
	@Override
	protected int getRequiredUserPerms() {
		return super.getRequiredUserPerms()|(requiresDJ()?ChatPermission.VC_SPEAK:ChatPermission.NONE);
	}

	protected abstract Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager);
	protected abstract boolean requiresDJ();
}
