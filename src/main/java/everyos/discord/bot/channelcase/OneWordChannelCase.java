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
import everyos.discord.bot.command.moderation.OneWordModerationCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.storage.database.DBObject;
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
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent().orElse("");
        String trunc = ArgumentParser.getIfPrefix(content, 
            new String[]{"---", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"});

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        return message.getChannel().flatMap(channel->{
	        return Mono.create(sink->{
	            String fromID = message.getChannelId().asString();
	            ChannelAdapter.of(data.shard, fromID).getData((obj, doc)->{
	                if (obj.has("data")) {
	                	//TODO: Check user ID
	                	if (!words.has(content.toLowerCase())) {
	    	        		sink.success(channel.createMessage(data.localize(LocalizedString.Undocumented)).then());
	    	        		return;
	    	        	}
	                	
	                	DBObject cc = obj.getOrDefaultObject("data", new DBObject());
	                	
	                	String uid = message.getAuthor().get().getId().asString();
	                	if (obj.getOrDefaultString("lastuser", "").equals(uid)) {
	                		sink.success(channel.createMessage(data.localize(LocalizedString.Undocumented)).then());
	    	        		return;
	                	}
	                	obj.set("lastuser", uid);
	                	
	                	StringBuilder sentenceb = new StringBuilder(cc.getOrDefaultString("sentence", ""));
	                	if (!content.isEmpty()&&(words.get(content.toLowerCase()).getAsInt()!=2))
	                		sentenceb.append(" ");
	                	sentenceb.append(content);
	                	String sentence = sentenceb.toString();
	                	int length = sentence.length();
	                	if (length>2000) sentence = sentence.substring(length-2000, length);
	                	cc.set("sentence", sentence);
	                	doc.save();
	                	sink.success(channel.createMessage(sentence));
	                } else sink.error(new Exception("An exception has occured!"));
	            });
	        })
	        .flatMap(mono->(Mono<?>) mono);
       });
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}