package everyos.discord.luwu.command.info;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.FillinUtil;
import everyos.discord.luwu.util.TimeUtil;
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