package everyos.discord.luwu.usercase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.channelcase.ChatLinkChannelCase;
import everyos.discord.luwu.channelcase.DefaultChannelCase;
import everyos.discord.luwu.channelcase.OneWordChannelCase;
import everyos.discord.luwu.channelcase.SuggestionsChannelCase;
import everyos.discord.luwu.channelcase.TicketChannelCase;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.localization.Localization;
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