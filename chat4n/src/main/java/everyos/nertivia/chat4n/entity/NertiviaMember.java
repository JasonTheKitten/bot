package everyos.nertivia.chat4n.entity;
		
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.nertivia.nertivia4j.entity.Member;
import everyos.nertivia.nertivia4j.entity.User;
import everyos.nertivia.nertivia4j.entity.channel.ServerChannel;
import reactor.core.publisher.Mono;

public class NertiviaMember extends NertiviaUser implements ChatMember {
	@SuppressWarnings("unused")
	private Member member;

	private NertiviaMember(ChatConnection connection, User user) {
		super(connection, user);
		if (user instanceof Member) member = (Member) user;
	}
	
	@Override public Mono<Boolean> isHigherThan(ChatMember chatMember) {
		return Mono.just(false);
	}
	
	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return super.supportsInterface(cls);
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return super.getInterface(cls);
	}
	
	public static ChatMember instatiate(ChatConnection connection, Member member) {
		return new NertiviaMember(connection, member);
	}

	public static Mono<ChatMember> instatiate(ChatConnection connection, User user, ChatChannel channel) {
		if (!(channel instanceof ServerChannel)) {
			return Mono.just(new NertiviaMember(connection, user)).cast(ChatMember.class);
		}
		return user.asMember(((ServerChannel) channel).getServerID())
			.map(member->new NertiviaMember(connection, user));
	}
}
