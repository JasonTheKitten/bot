package everyos.discord.luwu.command.channel;
	
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.MessageAdapter;
import everyos.discord.luwu.adapter.ModMemberAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.annotation.Ignorable;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBDocument;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Ignorable(id=7)
@Help(help=LocalizedString.StarboardCommandHelp, ehelp=LocalizedString.StarboardCommandExtendedHelp, category=CategoryEnum.Channel)
public class StarboardCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public StarboardCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand setCommand = new StarboardSetCommand();
        ICommand addCommand = new StarboardAddCommand();
        ICommand removeCommand = new StarboardRemoveCommand();
        ICommand ignoreCommand = new StarboardIgnoreCommand();
        ICommand unignoreCommand = new StarboardUnignoreCommand();
        ICommand emptyCommand = new StarboardEmptyCommand();
        ICommand configsCommand = new StarboardConfigInfoCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("set", setCommand);
        commands.put("add", addCommand);
        commands.put("remove", removeCommand);
        commands.put("ignore", ignoreCommand);
        commands.put("ignore", unignoreCommand);
        commands.put("empty", emptyCommand);
        commands.put("cinfo", configsCommand);
        lcommands.put(Localization.en_US, commands);
    }
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->PermissionUtil.check(member, new Permission[] {Permission.MANAGE_CHANNELS}))
                .flatMap(member->{
					if (argument.equals("")) {}; //TODO: Show help
			
			        String cmd = ArgumentParser.getCommand(argument);
			        String arg = ArgumentParser.getArgument(argument);
			        
			        ICommand command = lcommands.get(data.locale.locale).get(cmd);
			        
			        if (command==null)
			        	return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));
				    
			        return command.execute(message, data, arg);
                });
		});
	}

	@Override public HashMap<String, ICommand> getCommands(Localization locale) {
		return lcommands.get(locale);
	}
	
	public static DBArray createDefaultStars() {
		DBArray arr = new DBArray();
		arr.add(3);
		arr.add("\u2B50");
		return arr;
	}
	public static Mono<?> proccessStarboardReactions(BotInstance bot, Message message, ReactionEmoji remoji) {
		String emoji =
			remoji.asUnicodeEmoji().map(ue->ue.getRaw())
			.orElseGet(()->remoji.asCustomEmoji().map(ce->ce.getId().asString()).get());
		
		//Starboard code
		//TODO: Localize
		//TODO: Remove self stars
		
		//Get Message Data
		//If message original
		//  If is on starboard, return starboard
		//  If not on starboard, check eligibility
		//    Create starboard post
		//    Return starboard
		//If message starboard
		//  Return starboard
		//Get starboard and message
		//  Calculate reactions
		//  Edit message content
		
		//Get Message Data
		return message.getChannel()
			.filter(channel->channel instanceof GuildMessageChannel)
			.flatMap(channel->((GuildMessageChannel) channel).getGuild())
			.flatMap(guild->{
				return GuildAdapter.of(bot, guild).getDocument().flatMap(gdoc->{
					DBObject gobj = gdoc.getObject();
					if (!gobj.getOrDefaultString("star", "").equals(emoji)) return Mono.empty();
					return MessageAdapter.of(bot, message.getChannelId().asLong(), message.getId().asLong()).getDocument().flatMap(mdoc->{
						DBObject mobj = mdoc.getObject();
						if (!mobj.getOrDefaultBoolean("issb", false)) { //If message original
							if (mobj.has("sbmid")) //If is on starboard, return starboard
								return MessageAdapter.of(bot, mobj.getOrDefaultLong("sbcid", -1L), mobj.getOrDefaultLong("sbmid", -1L)).getDocument();
							
							//If not on starboard, check eligibility
							return checkUserIgnored(message.getReactors(remoji), bot, guild, gdoc)
								.filter(author->
									message.getAuthor().isPresent()&&
									!message.getAuthor().get().getId().equals(author.getId()))
								.count()
								.flatMap(count->{
									int reqcount = -1;
									DBArray stars = gobj.getOrDefaultArray("stars", StarboardCommand.createDefaultStars());
									for (int i=0; i<stars.getLength(); i+=2) {
										if (reqcount==-1||stars.getInt(i)<reqcount)
											reqcount = stars.getInt(i);
									}
									return (count<reqcount||reqcount==-1)?Mono.empty():guild.getChannelById(Snowflake.of(gobj.getOrDefaultLong("starc", -1L)));
								})
								.cast(GuildMessageChannel.class)
								.flatMap(channel->channel.createMessage(spec->{
									spec.setContent("Still processing starboard data, please wait");
									spec.setEmbed(embed->{
										embed.setColor(Color.YELLOW);
										embed.setDescription(message.getContent().replace("@", "@\u200B"));
										Set<Attachment> s = message.getAttachments();
										if (!s.isEmpty()) embed.setImage(s.iterator().next().getUrl());
										message.getAuthor().ifPresent(a->{
											String url = String.format(
												"https://discordapp.com/channels/%s/%s/%s", 
												guild.getId().asLong(), message.getId().asLong(), message.getId().asLong());
											embed.setAuthor(a.getUsername()+"#"+a.getDiscriminator(), url, a.getAvatarUrl());
											embed.setFooter("Posted by User ID: "+a.getId().asLong(), null);
										});
									});
								})) //Create starboard post
								.flatMap(sbmessage->{
									long cid = sbmessage.getChannelId().asLong();
									mobj.set("sbcid", cid);
									long mid = sbmessage.getId().asLong();
									mobj.set("sbmid", mid);
									
									return mdoc.save().then(sbmessage.addReaction(remoji)).then(MessageAdapter.of(bot, cid, mid).getDocument());
								}).flatMap(sdoc->{
									DBObject sobj = sdoc.getObject();
									sobj.set("issb", true);
									sobj.set("ogmid", message.getId().asLong());
									sobj.set("ogcid", message.getChannelId().asLong());
									
									return sdoc.save().then(Mono.just(sdoc)); //Return starboard
								});
						} else { //If message starboard
							return Mono.just(mdoc);
						}
					}).flatMap(sdoc->{
						DBObject sobj = sdoc.getObject();
						Snowflake originalChannelID = Snowflake.of(sobj.getOrDefaultLong("ogcid", -1L));
						Snowflake originalMessageID = Snowflake.of(sobj.getOrDefaultLong("ogmid", -1L));
						Snowflake starboardChannelID = Snowflake.of(sobj.getOrDefaultLong("cid", -1L));
						Snowflake starboardMessageID = Snowflake.of(sobj.getOrDefaultLong("mid", -1L));
						
						ArrayList<Long> users = new ArrayList<Long>(); //Calculate reactions
						return bot.client.getMessageById(originalChannelID, originalMessageID).flatMapMany(omessage->{
							return checkUserIgnored(omessage.getReactors(remoji), bot, guild, gdoc).doOnNext(user->{
								long uid = user.getId().asLong();
								if (!users.contains(uid)) users.add(uid);
							});
						}).then(bot.client.getMessageById(starboardChannelID, starboardMessageID)).flatMap(smessage->{
							return checkUserIgnored(smessage.getReactors(remoji), bot, guild, gdoc).map(user->{
								long uid = user.getId().asLong();
								if (!users.contains(uid)) users.add(uid);
								return true;
							}).last(true).flatMap(u->smessage.edit(spec->{ //Edit message content
								message.getAuthor().ifPresent(author->users.remove(author.getId().asLong()));
								int size = users.size();
								String emojiID = null;
								DBArray emojis = gdoc.getObject().getOrDefaultArray("stars", StarboardCommand.createDefaultStars());
								int areacts = 0;
								for (int i=0; i<emojis.getLength(); i+=2) {
									int v = emojis.getInt(i);
									if (v>areacts&&v<=size) {
										areacts = v;
										emojiID = emojis.getString(i+1);
									}
								}
								String emote = "";
								try {
									Long.valueOf(emojiID);
									emote = "<:star:"+emojiID+">";
								} catch (NumberFormatException ex) {
									emote = emojiID;
								}
								if (emote!=null) {
									spec.setContent(emote+" "+String.valueOf(size));
								} else {
									spec.setContent(String.valueOf(size));
								}
							}));
						});
					});
				});
			});
	}
	
	private static Flux<User> checkUserIgnored(Flux<User> reactors, BotInstance bot, Guild guild, DBDocument gdoc) {
		return reactors
			.filter(reactor->!reactor.isBot())
			.flatMap(reactor->ModMemberAdapter.of(bot, guild, reactor).getDocument().flatMap(doc->{
				return Mono.just(reactor);
			}));
	}
}

class StarboardSetCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				if (!parser.couldBeChannelID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				
				DBObject obj = doc.getObject();
				obj.set("starc", parser.eatChannelID());
				
				if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				boolean isID = parser.couldBeEmojiID();
				String reactID = isID?parser.eatEmojiID():parser.eat(); //TODO
				obj.set("star", reactID);
				
				obj.getOrCreateArray("stars", ()->StarboardCommand.createDefaultStars());
				
				return doc.save();
			}).then(channel.createMessage(data.localize(LocalizedString.StarboardSet)));
		});
	}
}
class StarboardAddCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				
				if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				int amount = (int) parser.eatNumerical();
				
				if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				boolean isID = parser.couldBeEmojiID();
				String starID = isID?parser.eatEmojiID():parser.eat(); //TODO
				
				DBArray stars = obj.getOrCreateArray("stars", ()->{
					return new DBArray();
				});
				
				if (stars.contains(amount)) return Mono.error(new LocalizedException(LocalizedString.StarAlreadyExistsForAmount));
				stars.add(amount);
				stars.add(starID);
				
				return doc.save();
			}).then(channel.createMessage(data.localize(LocalizedString.StarTypeAdd)));
		});
	}
}
class StarboardRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				
				if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				int amount = (int) parser.eatNumerical();
				
				DBArray stars = obj.getOrCreateArray("stars", ()->{
					return new DBArray();
				});
				
				for (int i=0; i<stars.getLength(); i+=2) {
					if (stars.getInt(i)==amount) {
						stars.remove(i);
						stars.remove(i);
						return doc.save().then(channel.createMessage(data.localize(LocalizedString.StarRemoved)));
					}
				}
				
				return Mono.error(new LocalizedException(LocalizedString.NoStarMatch));
			});
		});
	}
}
class StarboardIgnoreCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				
				if (!parser.couldBeChannelID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				
				return doc.save();
			}).then(channel.createMessage(data.localize(LocalizedString.StarboardIgnoredChannel)));
		});
	}
}
class StarboardUnignoreCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				
				if (!parser.couldBeChannelID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				
				return doc.save();
			}).then(channel.createMessage(data.localize(LocalizedString.StarboardUnignoredChannel)));
		});
	}
}
class StarboardEmptyCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
				doc.getObject().remove("stars");
				
				return doc.save();
			}).then(channel.createMessage(data.localize(LocalizedString.NoStarsLeft)));
		});
	}
}
class StarboardConfigInfoCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}
}