package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.GuildEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatServerCreateEvent;

public class DiscordServerCreateEvent extends DiscordServerEvent implements ChatServerCreateEvent {
    public DiscordServerCreateEvent(ChatConnection connection, GuildEvent event) {
        super(connection, event);
    }
}
