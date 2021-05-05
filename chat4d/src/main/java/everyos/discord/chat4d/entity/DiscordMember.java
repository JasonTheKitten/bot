package everyos.discord.chat4d.entity;

import java.util.Optional;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberRoleInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberVoiceConnectionInterface;
import everyos.discord.chat4d.PermissionUtil;
import everyos.discord.chat4d.functionality.member.DiscordGuildMemberModerationInterface;
import everyos.discord.chat4d.functionality.member.DiscordGuildMemberRoleInterface;
import everyos.discord.chat4d.functionality.member.DiscordGuildMemberVoiceConnectionInterface;
import reactor.core.publisher.Mono;

public class DiscordMember extends DiscordUser implements ChatMember {
	private Member member;

	private DiscordMember(ChatConnection connection, User member) {
		super(connection, member);
		if (member instanceof Member) this.member = (Member) member;
	}

	@Override
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return getInterface(cls)!=null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (member!=null) {
			if (cls==ChatMemberModerationInterface.class) {
				return (T) new DiscordGuildMemberModerationInterface(getConnection(), member);
			} else if (cls==ChatMemberVoiceConnectionInterface.class) {
				return (T) new DiscordGuildMemberVoiceConnectionInterface(getConnection(), member);
			} else if (cls==ChatMemberRoleInterface.class) {
				return (T) new DiscordGuildMemberRoleInterface(getConnection(), member);
			}
		}
		return super.getInterface(cls);
	}
	
	@Override
	public Mono<Boolean> isHigherThan(ChatMember chatMember) {
		if (!(chatMember instanceof DiscordMember)) {
			throw new IllegalArgumentException("Two incompatible objects being compared!");
		}
		return member.isHigher(((DiscordMember) chatMember).getMember())
			.doOnError((e)->e.printStackTrace());
	}
	
	public static ChatMember instatiate(ChatConnection connection, Member member) {
		return new DiscordMember(connection, member);
	}

	public static Mono<ChatMember> instatiate(ChatConnection connection, User user, ChatChannel cchannel) {
		if (!(cchannel instanceof DiscordChannel)) throw new IllegalArgumentException();
		Channel channel = ((DiscordChannel) cchannel).getChannel();
		if (!(channel instanceof GuildChannel)) {
			return Mono.just(new DiscordMember(connection, user));
		}
		return user.asMember(((GuildChannel) channel).getGuildId())
			.map(member->new DiscordMember(connection, member));
	}
	
	private Member getMember() {
		return this.member;
	}

	@Override
	public Mono<ChatGuild> getServer() {
		//TODO: Not available outside of guilds
		if (member==null) {
			return Mono.empty();
		}
		return member.getGuild().map(guild->new DiscordGuild(getConnection(), guild));
	}
	
	@Override
	public Optional<String> getNickname() {
		return member.getNickname();
	}
	
	@Override
	public long getJoinTime() {
		return member.getJoinTime().toEpochMilli();
	}

	@Override
	public Mono<Integer> getPermissions() {
		if (member==null) return Mono.just(Integer.MAX_VALUE);
		
		return member.getBasePermissions().map(PermissionUtil::fromNativePermissions);
	}
}
