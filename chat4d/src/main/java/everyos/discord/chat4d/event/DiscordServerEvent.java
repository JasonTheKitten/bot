package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.GuildEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatServerEvent;

public class DiscordServerEvent extends DiscordEvent implements ChatServerEvent {
    public DiscordServerEvent(ChatConnection connection, GuildEvent event) {
        super(connection, event);
    }
}
