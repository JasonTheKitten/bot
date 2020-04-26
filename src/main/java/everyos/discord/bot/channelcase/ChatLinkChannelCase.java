package everyos.discord.bot.channelcase;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.api.Moderation;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.channel.ResetChannelCommand;
import everyos.discord.bot.command.fun.MusicCommand;
import everyos.discord.bot.command.info.HelpCommand;
import everyos.discord.bot.command.moderation.BanCommand;
import everyos.discord.bot.command.moderation.ChatLinkManagerCommand;
import everyos.discord.bot.command.moderation.KickCommand;
import everyos.discord.bot.command.moderation.PurgeCommand;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.filter.StrictFilter;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.EmptyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChatLinkChannelCase implements IGroupCommand {
	private HashMap<String, ICommand> commands;
    public ChatLinkChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("link", new ChatLinkManagerCommand());
        commands.put("m", new MusicCommand());
        commands.put("ban", new BanCommand());
        commands.put("kick", new KickCommand());
        commands.put("help", new HelpCommand());
        commands.put("purge", new PurgeCommand());
        commands.put("resetchannel", new ResetChannelCommand());
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        String content = message.getContent();
        String trunc = ArgumentParser.getIfPrefix(content, data.prefixes);

        if (!(trunc == null)) {
            String command = ArgumentParser.getCommand(trunc);
            String arg = ArgumentParser.getArgument(trunc);

            if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        }

        long fromID = message.getChannelId().asLong();
        Flux<?> result = ChannelAdapter.of(data.bot, fromID).getDocument().flatMap(doc->{
        	DBObject obj = doc.getObject();
            if (obj.has("data")&&obj.getOrDefaultObject("data", null).has("chatlinkid")) {
            	DBObject dobj = obj.getOrDefaultObject("data", null);
            	if (!dobj.getOrDefaultBoolean("verified", false)) return Mono.error(new EmptyException());
                return Mono.just(dobj.getOrDefaultLong("chatlinkid", -1L));
            }
            return Mono.error(new Exception("An exception has occured!"));
        })
        .flatMapMany(s->{
        	ChatLinkAdapter adapter = ChatLinkAdapter.of(data.bot, s);
        	
        	String msgcontent =
    			Optional.ofNullable(message.getContent().isEmpty()?null:message.getContent()).map(c->"> "+c.replace("\n", "\n> ")).orElse("");
    		
    		String header = String.format("**%s** (%s)\n",
            		message.getAuthor().map(a->a.getUsername()+"#"+a.getDiscriminator()).orElse("A mysterious user").replace("*", "\\*"),
            		message.getAuthor().map(a->a.getId().asString()).orElse("Unknown"));
    		boolean forceHeader = false;
    		
    		long uid = message.getAuthor().map(a->a.getId().asLong()).orElse(-1L);
    		long time = System.currentTimeMillis();
    		long channelID;
    		synchronized(adapter) {
    			channelID = adapter.lastChannel;
    			
    			if (
    				!(uid==adapter.lastUserID) ||
    				time-adapter.lastTime>120*1000 ||
    				adapter.lastUserID<0 ||
    				adapter.lastTime<0 ||
    				channelID == -1
    			) forceHeader = true;
    			adapter.lastUserID = uid;
    			adapter.lastTime = time;
    			adapter.lastChannel = message.getChannelId().asLong();
    		}
    		final boolean fHF = forceHeader;
    		
    		Flux<Message> clsend = adapter.forward(message.getChannelId().asLong(), (msg, cid)->{
        		StringBuilder formatted = new StringBuilder();
        		if (fHF || cid == channelID) formatted.append(header);
        		formatted.append(StrictFilter.filter.filter(msgcontent).replace(".", "\u200B."));
        		
        		msg.setContent(formatted.toString());
        		message.getAttachments().forEach(att->{
        			try {
        				URLConnection con = new URL(att.getProxyUrl()).openConnection();
        				con.setRequestProperty("User-Agent", "discord-bot (Java)");
						if (att.isSpoiler()) {
							msg.addFileSpoiler(att.getFilename(), con.getInputStream());
						} else {
							msg.addFile(att.getFilename(), con.getInputStream());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
        		});
        	});
    		
    		String key = data.bot.moderationKey;
    		AtomicBoolean isFineContent = new AtomicBoolean(true);
    		Set<Attachment> attachments = message.getAttachments();
    		String[] attachmentURLs = new String[attachments.size()];
    		int i = 0;
    		for (Attachment attachment: attachments) {
    			attachmentURLs[i++] = attachment.getUrl();
    		}
    		return Flux.fromArray(attachmentURLs).flatMap(url->Moderation.isImageSafe(key, url)).doOnNext(resp->{
    			isFineContent.set(isFineContent.get()&&resp);
    		}).last(true).flatMapMany(o->{
    			if (isFineContent.get()==false) return message.addReaction(Unicode.of(null, "\u274C", false)).then().flux();
    			return clsend;
    		});
        });
        
        ReactionEmoji emoji = Unicode.of(null, "\u2705", false);
        return result.last().flatMap(o->
        	message.addReaction(emoji).then(Mono.just(true)).delayElement(Duration.ofMillis(1000))
        	.then(message.removeSelfReaction(emoji)));
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}
