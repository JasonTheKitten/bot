package everyos.discord.chat4d.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.channel.ChannelCreateSpec;
import everyos.discord.chat4d.PermissionUtil;
import reactor.core.publisher.Mono;

public class DiscordGuild implements ChatGuild {
	private Guild guild;
	private ChatConnection connection;

	public DiscordGuild(ChatConnection connection, Guild guild) {
		this.connection = connection;
		this.guild = guild;
	}

	@Override
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override
	public ChatClient getClient() {
		return connection.getClient();
	}

	@Override
	public long getID() {
		return guild.getId().asLong();
	}

	@Override
	public String getName() {
		return guild.getName();
	}

	//TODO: Should this be under an interface?
	@Override
	public Mono<ChatChannel> createChannel(Consumer<ChannelCreateSpec> func) {
		return guild.createTextChannel(spec->{
			Set<PermissionOverwrite> overrides = new HashSet<PermissionOverwrite>();
			
			func.accept(new ChannelCreateSpec() {
				@Override
				public void setName(String name) {
					spec.setName(name);
				}
				
				@Override
				public void setTopic(String topic) {
					spec.setTopic(topic);
				}

				@Override
				public void setReason(String reason) {
					spec.setReason(reason);
				}

				@Override
				public void setRoleOverride(long roleID, int allow, int deny) {
					if (roleID == -1) roleID = guild.getId().asLong();
					
					overrides.add(PermissionOverwrite.forRole(
						Snowflake.of(roleID),
						PermissionUtil.getNativePermissions(allow),
						PermissionUtil.getNativePermissions(deny)));
				}

				@Override
				public void setMemberOverride(long memberID, int allow, int deny) {
					overrides.add(PermissionOverwrite.forMember(
						Snowflake.of(memberID),
						PermissionUtil.getNativePermissions(allow),
						PermissionUtil.getNativePermissions(deny)));
				}
			});
			
			spec.setPermissionOverwrites(overrides);
		}).map(channel -> new DiscordChannel(connection, channel));
	}

}
