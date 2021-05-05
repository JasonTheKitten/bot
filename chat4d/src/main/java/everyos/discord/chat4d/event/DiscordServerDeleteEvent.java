package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.GuildDeleteEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.event.ChatServerDeleteEvent;
import everyos.discord.chat4d.entity.DiscordGuild;
import reactor.core.publisher.Mono;

public class DiscordServerDeleteEvent extends DiscordServerEvent implements ChatServerDeleteEvent {
    private GuildDeleteEvent event;

	public DiscordServerDeleteEvent(ChatConnection connection, GuildDeleteEvent event) {
        super(connection, event);
        this.event = event;
    }

	@Override
	public Mono<ChatGuild> getGuild() {
		return Mono.justOrEmpty(event.getGuild())
			.map(guild->new DiscordGuild(getConnection(), guild));
	}
}
