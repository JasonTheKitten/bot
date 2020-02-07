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
    public int money;
    public long dailytimestamp;

	public UserObject(GuildObject guild, Member user) {
		this.guild = guild;
		this.user = user;
		this.id = UserHelper.getUserId(user);
        this.opted = false;
        this.i = 0;
        this.money = 0;
        this.dailytimestamp = -(24*60*60*1000);
	}
	
	public UserObject(GuildObject guild, JsonObject save) {
		this.guild = guild;
		this.id = save.get("id").getAsString();
		this.user = guild.guild.getMemberById(Snowflake.of(this.id)).block();
        this.opted = save.get("opted").getAsBoolean();
        this.i = save.has("i")?save.get("i").getAsInt():0;
        this.money = save.has("money")?save.get("money").getAsInt():0;
        this.dailytimestamp = save.has("dailytimestamp")?save.get("dailytimestamp").getAsLong():-(24*60*60*1000);
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
		}).subscribe();
	}
	public void ban() { ban(null, 0); }

	public void kick(String msg) {
		this.user.kick(msg).subscribe();
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
        save.put("id", this.id);
        save.put("opted", this.opted);
        save.put("i", this.i);
        save.put("money", this.money);
        save.put("dailytimestamp", this.dailytimestamp);
		return save;
	}

    public boolean isBot() {
        return this.user.isBot();
    }
}
