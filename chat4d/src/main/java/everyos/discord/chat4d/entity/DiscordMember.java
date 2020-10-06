package everyos.discord.chat4d.entity;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.discord.chat4d.functionality.member.DiscordGuildMemberModerationInterface;
import reactor.core.publisher.Mono;

public class DiscordMember extends DiscordUser implements ChatMember {
	private Member member;

	private DiscordMember(ChatConnection connection, User member) {
		super(connection, member);
		if (member instanceof Member) this.member = (Member) member;
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return super.supportsInterface(cls);
	}

	@SuppressWarnings("unchecked")
	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (cls==ChatMemberModerationInterface.class) {
			return (T) new DiscordGuildMemberModerationInterface(getConnection(), member);
		}
		return super.getInterface(cls);
	}
	
	@Override public Mono<Boolean> isHigherThan(ChatMember chatMember) {
		if (!(chatMember instanceof DiscordMember)) {
			throw new IllegalArgumentException("Two incompatible objects being compared!");
		}
		return member.isHigher(((DiscordMember) chatMember).getMember())
			.doOnNext(a->System.out.println(a))
			.doOnError((e)->e.printStackTrace());
	}
	
	public static ChatMember instatiate(ChatConnection connection, Member member) {
		return new DiscordMember(connection, member);
	}

	public static Mono<ChatMember> instatiate(ChatConnection connection, User user, ChatChannel cchannel) {
		if (!(cchannel instanceof DiscordChannel)) throw new IllegalArgumentException();
		Channel channel = ((DiscordChannel) cchannel).getChannel();
		if (!(channel instanceof GuildChannel)) {
			return Mono.just(new DiscordMember(connection, user)).cast(ChatMember.class);
		}
		return user.asMember(((GuildChannel) channel).getGuildId())
			.map(member->new DiscordMember(connection, member));
	}
	
	private Member getMember() {
		return this.member;
	}
}
