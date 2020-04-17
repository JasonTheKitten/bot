package everyos.discord.bot.command.info;

import discord4j.core.object.entity.Message;
import discord4j.gateway.GatewayClient;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.PingCommandHelp, ehelp = LocalizedString.PingCommandExtendedHelp, category=CategoryEnum.Info)
public class PingCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
    	GatewayClient cli = data.shard.client.getGatewayClient(data.event.getShardInfo().getIndex()).get();
        return message.getChannel()
            .flatMap(channel->channel.createMessage(
                data.locale.localize(LocalizedString.Ping,
                    FillinUtil.of("ping", String.valueOf(cli.getResponseTime().toMillis())))));
    }
}