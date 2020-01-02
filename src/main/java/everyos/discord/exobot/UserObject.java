package everyos.discord.exobot;

import discord4j.core.object.entity.Member;
import everyos.discord.exobot.util.UserHelper;
import reactor.core.publisher.Mono;

public class UserObject {
	public GuildObject guild;
	public Member user;
	public String id;
	public boolean opted;

	public UserObject(GuildObject guild, Member user) {
		this.guild = guild;
		this.user = user;
		this.id = UserHelper.getUserId(user);
		this.opted = false;
	}
	
	public GlobalUserObject toGlobal() {
		return new GlobalUserObject(this.user); //TODO: Add to Statics list
	}
	
	public boolean isOpted() {
		return isHigherThanBot()||this.opted;
	}
	public boolean isHigherThanBot() {
		return this.user.isHigher(Statics.client.getSelfId().get()).block();
	}
	public void ban(String msg, int days) {
		this.user.ban(reason->{
			if (msg!=null&&!msg.equals("")) {
				reason.setReason(msg);
				if (days!=0) reason.setDeleteMessageDays(days);
			}
		}).block();
	}
	public void ban() { ban(null, 0); }

	public void kick(String msg) {
		this.user.kick(msg).block();
	}
	public void kick() { kick(null); }

	public boolean isHigherThan(UserObject user) {
		return this.user.isHigher(user.user).block();
	}
	public boolean isHigherThan(Member user) {
		return this.user.isHigher(user).block();
	}
	public boolean isHigherThan(Mono<Member> user) {
		return this.user.isHigher(user.block()).block();
	}

	public String serializeSave() {
		return "{\"id\":\""+id+"\",\"opted\":"+opted+"}";
	}
}
