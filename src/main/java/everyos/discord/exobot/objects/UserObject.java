package everyos.discord.exobot.objects;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.UserHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;
import reactor.core.publisher.Mono;

public class UserObject {
	public GuildObject guild;
	public Member user;
	public String id;
    public boolean opted;
    public int i;

	public UserObject(GuildObject guild, Member user) {
		this.guild = guild;
		this.user = user;
		this.id = UserHelper.getUserId(user);
		this.opted = false;
	}
	
	public UserObject(GuildObject guild, JsonObject save) {
		this.guild = guild;
		this.id = save.get("id").getAsString();
		this.user = guild.guild.getMemberById(Snowflake.of(this.id)).block();
        this.opted = save.get("opted").getAsBoolean();
        this.i = save.has("i")?save.get("i").getAsInt():0;
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

	public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("id", id);
        save.put("opted", opted);
        save.put("i", this.i);
		return save;
	}
}
