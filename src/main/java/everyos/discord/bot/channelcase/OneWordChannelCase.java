package everyos.discord.bot.channelcase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.channel.ResetChannelCommand;
import everyos.discord.bot.command.info.HelpCommand;
import everyos.discord.bot.command.moderation.BanCommand;
import everyos.discord.bot.command.moderation.KickCommand;
import everyos.discord.bot.command.moderation.OneWordModerationCommand;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

public class OneWordChannelCase implements IGroupCommand {
	private static JsonObject words;
	static {
		try {
            InputStream in = ClassLoader.getSystemResourceAsStream("whitelist.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            words = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
        } catch (Exception e) { e.printStackTrace(); }
	}
	
    private HashMap<String, ICommand> commands;
    public OneWordChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("oneword", new OneWordModerationCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
        commands.put("resetchannel", new ResetChannelCommand());
        commands.put("help", new HelpCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent();
        String trunc = ArgumentParser.getIfPrefix(content, data.prefixes);

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        return message.getChannel().flatMap(channel->{
            long fromID = channel.getId().asLong();
            return ChannelAdapter.of(data.bot, fromID).getDocument().flatMap(doc->{
            	DBObject obj = doc.getObject();
                if (obj.has("data")) {
                	//TODO: Pin data when we get too long
                	if (!words.has(content.toLowerCase())) return Mono.error(new LocalizedException(LocalizedString.OneWordNotRecognized));
                	
                	DBObject cc = obj.getOrDefaultObject("data", null);
                	
                	long uid = message.getAuthor().get().getId().asLong();
                	if (cc.getOrDefaultLong("lastuser", -1L)==uid)
                		return Mono.error(new LocalizedException(LocalizedString.OneWordOneWord));
                	
                	cc.set("lastuser", uid);
                	
                	StringBuilder sentenceb = new StringBuilder(cc.getOrDefaultString("sentence", ""));
                	if (!content.isEmpty()&&(words.get(content.toLowerCase()).getAsInt()!=2))
                		sentenceb.append(" ");
                	sentenceb.append(content);
                	String sentence = sentenceb.toString();
                	int length = sentence.length();
                	if (length>2000) sentence = sentence.substring(length-2000, length);
                	cc.set("sentence", sentence);
                	return doc.save().then(channel.createMessage(sentence));
                } else return Mono.error(new Exception("An exception has occured!"));
	        }).cast(Message.class).onErrorResume(e->{
	     	   if (e instanceof UnrecognizedWordException) {
	    		   return channel.createMessage(data.localize(LocalizedString.UnrecognizedWord));
	    	   }
	    	   return Mono.error(e);
	       });
       });
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}

class UnrecognizedWordException extends Exception {
	private static final long serialVersionUID = 2003475837972589674L;
}