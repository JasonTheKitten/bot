package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.GuildDeleteEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatServerDeleteEvent;

public class DiscordServerOutageEvent extends DiscordServerEvent implements ChatServerDeleteEvent {
    public DiscordServerOutageEvent(ChatConnection connection, GuildDeleteEvent event) {
        super(connection, event);
    }
}
