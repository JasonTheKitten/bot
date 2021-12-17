package everyos.nertivia.chat4n.entity;
		
import java.util.Optional;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.nertivia.chat4n.functionality.member.NertiviaMemberModerationInterface;
import everyos.nertivia.nertivia4j.entity.Member;
import everyos.nertivia.nertivia4j.entity.User;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import everyos.nertivia.nertivia4j.entity.channel.ServerChannel;
import reactor.core.publisher.Mono;

public class NertiviaMember extends NertiviaUser implements ChatMember {
	private Member member;

	private NertiviaMember(ChatConnection connection, User user) {
		super(connection, user);
		if (user instanceof Member) member = (Member) user;
	}
	
	@Override public Mono<Boolean> isHigherThan(ChatMember chatMember) {
		return Mono.just(false);
	}
	
	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		if (cls==ChatMemberModerationInterface.class) {
			return true;
		}
		return super.supportsInterface(cls);
	}

	@SuppressWarnings("unchecked")
	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (cls==ChatMemberModerationInterface.class) {
			return (T) new NertiviaMemberModerationInterface(getConnection(), member);
		}
		return super.getInterface(cls);
	}
	
	public static ChatMember instatiate(ChatConnection connection, Member member) {
		return new NertiviaMember(connection, member);
	}

	public static Mono<ChatMember> instatiate(ChatConnection connection, User user, NertiviaChannel cchannel) {
		Channel channel = cchannel.getRaw();
		if (!(channel instanceof ServerChannel)) {
			return Mono.just(new NertiviaMember(connection, user));
		}
		return user.asMember(((ServerChannel) channel).getServerID())
			.map(member->new NertiviaMember(connection, member));
	}

	@Override
	public Mono<ChatGuild> getServer() {
		return member.getServer().map(server->new NertiviaServer(getConnection(), server));
	}

	@Override
	public Mono<Integer> getPermissions() {
		return Mono.just(Integer.MAX_VALUE);
	}

	@Override
	public Optional<String> getNickname() {
		return Optional.empty(); //TODO
	}

	@Override
	public Optional<Long> getJoinTime() {
		return Optional.of(0L);
	}
}
