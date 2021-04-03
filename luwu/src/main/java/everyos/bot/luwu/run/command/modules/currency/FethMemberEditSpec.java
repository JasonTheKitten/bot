package everyos.bot.luwu.run.command.modules.currency;

public interface FethMemberEditSpec {
	void setCurrency(long currency);
	void setTimeout(long timeout);
	void setCooldown(long cooldown);
	FethMemberInfo getInfo();
}
