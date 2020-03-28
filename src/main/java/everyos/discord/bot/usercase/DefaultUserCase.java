package everyos.discord.bot.usercase;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.ShardInstance;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.channelcase.ChatLinkChannelCase;
import everyos.discord.bot.channelcase.DefaultChannelCase;
import everyos.discord.bot.channelcase.OneWordChannelCase;
import everyos.discord.bot.channelcase.SuggestionsChannelCase;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import reactor.core.publisher.Mono;

public class DefaultUserCase implements IGroupCommand {
	private ShardInstance shard = null;
	private HashMap<String, ICommand> channels;
	
	public DefaultUserCase(ShardInstance shard) {
		this.shard = shard;
		
		channels = new HashMap<String, ICommand>();
        channels.put("default", new DefaultChannelCase());
        channels.put("suggestions", new SuggestionsChannelCase());
        channels.put("chatlink", new ChatLinkChannelCase());
        channels.put("oneword", new OneWordChannelCase());
	}
	
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
    	AtomicReference<String> cmode = new AtomicReference<String>();
        ChannelAdapter.of(shard, message.getChannelId().asString()).getDocument().getObject(obj->{
            cmode.set(obj.getOrDefaultString("type", "default"));
        });
        LocalizationProvider provider = new LocalizationProvider(Localization.en_US);
        //CommandData data = new CommandData(provider, shard);

        return
            channels.getOrDefault(cmode.get(), channels.get("default"))
                .execute(message, data, message.getContent().orElse(""))
            .onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
    }
}