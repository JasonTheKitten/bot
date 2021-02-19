package everyos.bot.chat4j.entity;

public final class ChatPermission {
	public static final int NONE = 0;
	
	public static final int SEND_MESSAGES = 1<<0;
	public static final int KICK_MEMBERS = 1<<1;
	public static final int BAN_MEMBERS = 1<<2;
	public static final int SEND_EMBEDS = 1<<3;
	public static final int ADD_REACTIONS = 1<<4;
	public static final int REMOVE_ALL_REACTIONS = 1<<4;
	public static final int VC_CONNECT = 1<<6;
	public static final int VC_SPEAK = 1<<7;
	public static final int MANAGE_ROLES = 1<<8;
	public static final int MANAGE_MESSAGES = 1<<9;
	public static final int MANAGE_CHANNELS = 1<<10;
	public static final int MANAGE_GUILD = 1<<11;
	public static final int MANAGE_EMOJIS = 1<<12;
	public static final int USE_EXTERNAL_EMOJIS = 1<<13;
	public static final int VC_PRIORITY = 1<<14;
	public static final int MANAGE_MEMBERS = 1<<15;

	/**
	 * Checks for missing permissions, given a set and subset
	 * @param set The permissions required
	 * @param subset The permissions had
	 * @return Which permissions are contained by set but not by subset.
	 */
	public static int contains(int set, int subset) {
		return set&(set^subset);
	}
}
