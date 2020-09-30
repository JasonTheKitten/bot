package everyos.bot.luwu.entity;

import everyos.bot.chat4j.enm.ChatPermission;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import everyos.bot.luwu.database.Database;
import reactor.core.publisher.Mono;

/**
 * Represents a member of either a channel or guild, depending on whether the channel has an associated guild
 * Stand-alone channels have their own "guilds"
 */
public class Member extends User implements ChatInterfaceProvider {
	private ChatMember member;
	public Member(ChatMember member, Database database) {
		super(member, database);
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

	public long getID() {
		return this.member.getID();
	}
}
