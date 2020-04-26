package everyos.discord.bot.usercase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.channelcase.ChatLinkChannelCase;
import everyos.discord.bot.channelcase.DefaultChannelCase;
import everyos.discord.bot.channelcase.OneWordChannelCase;
import everyos.discord.bot.channelcase.SuggestionsChannelCase;
import everyos.discord.bot.channelcase.TicketChannelCase;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import reactor.core.publisher.Mono;

public class DefaultUserCase implements IGroupCommand {
	private BotInstance bot = null;
	private HashMap<String, ICommand> channels;
	
	public DefaultUserCase(BotInstance bot) {
		this.bot = bot;
		
		channels = new HashMap<String, ICommand>();
        channels.put("default", new DefaultChannelCase());
        channels.put("suggestions", new SuggestionsChannelCase());
        channels.put("chatlink", new ChatLinkChannelCase());
        channels.put("oneword", new OneWordChannelCase());
        channels.put("ticket", new TicketChannelCase());
	}
	
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return ChannelAdapter.of(bot, message.getChannelId().asLong()).getDocument().flatMap(document->{
	        data.channelcase = (IGroupCommand) channels.getOrDefault(
	        	document.getObject().getOrDefaultString("type", "default"), channels.get("default"));
	        
	        return data.channelcase.execute(message, data, message.getContent());
        });
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) {return channels;}
}