package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.commands.LocalizedCommandWrapper;
import everyos.discord.bot.commands.moderation.AnnounceCommand;
import everyos.discord.bot.commands.moderation.BanCommand;
import everyos.discord.bot.commands.moderation.KickCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.FillinUtil;

public class SuggestionChannelCase implements IChannelCase {
	private static HashMap<String, LocalizedCommandWrapper> commands;
	static {
		ICommand banCommand = new BanCommand();
		ICommand kickCommand = new KickCommand();
		ICommand announceCommand = new AnnounceCommand();
		
        commands = new HashMap<String, LocalizedCommandWrapper>();
        
		commands.put("ban", new LocalizedCommandWrapper(banCommand, Localization.en_US));
		commands.put("kick", new LocalizedCommandWrapper(kickCommand, Localization.en_US));
		commands.put("announce", new LocalizedCommandWrapper(announceCommand, Localization.en_US));
	}
	
    @Override public void execute(Message message, MessageAdapter adapter) {
    	String msg = message.getContent().orElse("<No Content>");
    	ArgumentParser.ifPrefix(msg, adapter.getPreferredPrefix(), content->{
    		String command = ArgumentParser.getCommand(content);
            String argument = ArgumentParser.getArgument(content);
            if (commands.containsKey(command))
                commands.get(command).execute(message, adapter, argument);
    		return commands.containsKey(command);
    	}).elsedo(()->{
	        message.delete().subscribe();
	        //TODO: Allow moderation commands?
	        //TODO: Filter
	        adapter.getChannelAdapter(cadapter->{
	            adapter.getTextLocale(locale-> {
	                cadapter.sendEmbed(embed -> {
	                    User usr = message.getAuthor().orElse(null);
	                    String usrn = (usr==null)?"<Unknown Author>":usr.getUsername();
	                    String usrid = (usr==null)?"<Unknown Author>":usr.getId().asString();
	                    embed.setTitle(adapter.formatTextLocale(locale, 
	                        LocalizedString.SuggestionBy, 
	                        FillinUtil.of("user", usrn)));
	                    embed.setDescription(msg);
	                    embed.setFooter(adapter.formatTextLocale(locale, 
	                        LocalizedString.SuggestionFooter,
	                        FillinUtil.of("id", usrid)), null);
	                }, embed->{
	                    embed.addReaction(ReactionEmoji.unicode("\u2705")).subscribe();
	                    embed.addReaction(ReactionEmoji.unicode("\u274C")).subscribe();
	                    message.getAuthor().ifPresent(author->{
	                        cadapter.send(author.getMention(), ping->ping.delete().subscribe());
	                    });
	                });
	            });
	        });
    	});
    }
}