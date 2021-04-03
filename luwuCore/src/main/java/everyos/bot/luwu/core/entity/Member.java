package everyos.bot.luwu.core.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberRoleInterface;
import everyos.bot.chat4j.functionality.member.ChatMemberVoiceConnectionInterface;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.imp.MemberModerationInterfaceImp;
import everyos.bot.luwu.core.entity.imp.MemberRoleInterfaceImp;
import everyos.bot.luwu.core.entity.imp.MemberVoiceConnectionInterfaceImp;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import everyos.bot.luwu.core.functionality.member.MemberModerationInterface;
import everyos.bot.luwu.core.functionality.member.MemberRoleInterface;
import everyos.bot.luwu.core.functionality.member.MemberVoiceConnectionInterface;
import reactor.core.publisher.Mono;

/**
 * Represents a member of either a channel or guild, depending on whether the channel has an associated guild
 * Stand-alone channels have their own "guilds"
 */
public class Member extends User implements InterfaceProvider {
	private ChatMember member;
	public Member(Connection connection, ChatMember member) {
		this(connection, member, null);
	}

	public Member(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		super(connection, member, documents);
		this.member = member;
	}

	public Mono<Boolean> hasPermissions(ChatPermission permissions) {
		return Mono.just(true); //TODO: Actual imp
	};
	public Mono<Integer> getPermissions() {
		return member.getPermissions();
	};
	public Mono<Boolean> isHigherThan(Member member) {
		return this.member.isHigherThan(member.getChatMember());
	};
	
	public Mono<Server> getServer() {
		return member.getServer()
			.map(member->new Server(getConnection(), member));
	}
	
	public Optional<String> getNickname() {
		return member.getNickname();
	}

	@Override
	public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return
			(cls==MemberVoiceConnectionInterface.class&&member.supportsInterface(ChatMemberVoiceConnectionInterface.class))||
			(cls==MemberModerationInterface.class&&member.supportsInterface(ChatMemberModerationInterface.class))||
			(cls==MemberRoleInterface.class&&member.supportsInterface(ChatMemberRoleInterface.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Interface> T getInterface(Class<T> cls) {
		if (cls==MemberVoiceConnectionInterface.class) {
			return (T) new MemberVoiceConnectionInterfaceImp(
				getConnection(),
				member.getInterface(ChatMemberVoiceConnectionInterface.class));
		} else if (cls==MemberModerationInterface.class) {
			return (T) new MemberModerationInterfaceImp(
				getConnection(),
				member.getInterface(ChatMemberModerationInterface.class));
		} else if (cls==MemberRoleInterface.class) {
			return (T) new MemberRoleInterfaceImp(
				getConnection(),
				member.getInterface(ChatMemberRoleInterface.class));
		}
		return null;
	};
	
	protected ChatMember getChatMember() {
		return this.member;
	}
	
	protected Mono<DBDocument> getLocalDocument() {
		return getNamedDocument("members");
	}
	
	public <T extends Member> T as(MemberFactory<T> factory) {
		return factory.create(getConnection(), member, getDocuments());
	}
	
	public long getJoinTimestamp() {
		return member.getJoinTime();
	}

	public static String[] convertPermsToNames(int perms) {
		List<String> names = new ArrayList<>();
		
		for (int i=0; i<permNames.length; i++) {
			if (permNames[i] == null) continue;
			
			if ((1<<i&perms)!=0) {
				names.add(permNames[i]);
			}
		}
		
		return names.toArray(new String[names.size()]);
	}
	
	private static final String[] permNames = new String[] {
		"perm.sendmessages", "perm.kickmembers", "perm.banmembers", "perm.sendembeds", "perm.addreactions",
		"perm.removeallreactions", "perm.vcconnect", "perm.vcspeak", "perm.manageroles", "perm.managemessages",
		"perm.managechannels", "perm.manageguild", "perm.manageemojis", "perm.externalemojis", "perm.vcpriority",
		"perm.managemembers"
	};
}
