package everyos.bot.chat4j.functionality.channel;

public interface ChannelCreateSpec {
	void setName(String name);
	void setTopic(String localize);
	void setReason(String string);
	
	void setRoleOverride(long roleID, int allow, int deny);
	void setMemberOverride(long long1, int userRoles, int none);
}
