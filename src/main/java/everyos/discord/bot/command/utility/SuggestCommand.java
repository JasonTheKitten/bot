package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.SuggestCommandHelp, ehelp = LocalizedString.SuggestCommandExtendedHelp, category=CategoryEnum.Utility)
public class SuggestCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getAuthorAsMember().flatMap(author->
            message.getChannel().flatMap(c->
            	suggest(author, c, data, argument)));
    }
    public static Mono<Message> suggest(Member author, MessageChannel channel, CommandData data, String argument) {
        return channel.createEmbed(embed->{
            embed.setTitle(data.safe(LocalizedString.SuggestionBy, FillinUtil.of("user", author.getUsername())));
            embed.setDescription(data.safe(argument));
            embed.setFooter(data.locale.localize(LocalizedString.SuggestionFooter, FillinUtil.of("id", author.getId().asString())), null);
        }).flatMap(message->{
            return message.addReaction(ReactionEmoji.unicode("\u2705")).and(
            	message.addReaction(ReactionEmoji.unicode("\u274C")))
            .then(Mono.just(message));
        });
    }
}