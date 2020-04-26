package everyos.discord.bot.command.info;

import discord4j.core.object.entity.Message;
import discord4j.gateway.GatewayClient;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.FillinUtil;
import everyos.discord.bot.util.TimeUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.UptimeCommandHelp, ehelp = LocalizedString.UptimeCommandExtendedHelp, category=CategoryEnum.Info)
public class UptimeCommand implements ICommand {
    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{	
            long m = System.currentTimeMillis();
            //long uptime = m - data.bot.instance.uptime;
            long cuptime = m - data.bot.uptime;
            
            /*String hours = String.valueOf(TimeUtil.getHours(uptime, false));
            String minutes = String.valueOf(TimeUtil.getMinutes(uptime, true));
            String seconds = String.valueOf(TimeUtil.getSeconds(uptime, true));*/
            
            String chours = String.valueOf(TimeUtil.getHours(cuptime, false));
            String cminutes = String.valueOf(TimeUtil.getMinutes(cuptime, true));
            String cseconds = String.valueOf(TimeUtil.getSeconds(cuptime, true));

            return channel.createMessage(
                data.localize(LocalizedString.Uptime,
                    FillinUtil.of(/*"h", hours, "m", minutes, "s", seconds,*/
                        "ch", chours, "cm", cminutes, "cs", cseconds)));
        });
    }
}