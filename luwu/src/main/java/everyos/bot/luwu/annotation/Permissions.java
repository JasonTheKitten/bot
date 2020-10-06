package everyos.bot.luwu.annotation;

import everyos.bot.chat4j.enm.ChatPermission;

public @interface Permissions {
	public ChatPermission[] permissions();
}
