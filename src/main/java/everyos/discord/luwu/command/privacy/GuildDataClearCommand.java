package everyos.discord.luwu.command.privacy;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(category=CategoryEnum.Privacy)
public class GuildDataClearCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember()
			.flatMap(member->PermissionUtil.check(member, Permission.MANAGE_GUILD))
			.then(message.getChannel()).cast(GuildMessageChannel.class)
			.flatMap(channel->{
				if (!(argument.equals("-f")||argument.equals("-force"))) return Mono.error(new LocalizedException(LocalizedString.MustForce));
				
				return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).wipe()
					.then(channel.createMessage(data.localize(LocalizedString.DataWiped)));
			});
	}
}
