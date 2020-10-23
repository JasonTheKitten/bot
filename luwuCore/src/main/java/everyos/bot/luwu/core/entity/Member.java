package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.enm.ChatPermission;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import reactor.core.publisher.Mono;

/**
 * Represents a member of either a channel or guild, depending on whether the channel has an associated guild
 * Stand-alone channels have their own "guilds"
 */
public class Member extends User implements ChatInterfaceProvider {
	private ChatMember member;
	public Member(Connection connection, ChatMember member) {
		super(connection, member);
		this.member = member;
	}

	public Mono<Boolean> hasPermissions(ChatPermission[] permissions) {
		return Mono.just(true); //TODO: Actual imp
	};
	public Mono<ChatPermission[]> getPermissions() {
		return Mono.empty();
	};
	public Mono<Boolean> isHigherThan(Member member) {
		return this.member.isHigherThan(member.getChatMember());
	};

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return member.supportsInterface(cls);
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return member.getInterface(cls);
	};
	
	protected ChatMember getChatMember() {
		return this.member;
	}

	public UserID getID() {
		return new UserID() {
			@Override public long getLong() {
				return member.getID();
			}
		};
	}

	//Luwu-specific logic
	public Mono<Boolean> isDJ() {
		return Mono.just(true);
	}
}
