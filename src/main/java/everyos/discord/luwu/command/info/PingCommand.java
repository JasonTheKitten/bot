package everyos.discord.luwu.command.info;

import discord4j.core.object.entity.Message;
import discord4j.gateway.GatewayClient;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.PingCommandHelp, ehelp = LocalizedString.PingCommandExtendedHelp, category=CategoryEnum.Info)
public class PingCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
    	GatewayClient cli = data.bot.client.getGatewayClient(data.event.getShardInfo().getIndex()).get();
        return message.getChannel()
            .flatMap(channel->channel.createMessage(
                data.localize(LocalizedString.Ping,
                    FillinUtil.of("ping", String.valueOf(cli.getResponseTime().toMillis())))));
    }
}