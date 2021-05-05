package everyos.bot.luwu.run.command.modules.privacy;

import everyos.bot.luwu.core.command.CommandContainer;

public class PrivacyCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.privacypolicy", new PrivacyPolicyCommand());
		commands.registerCommand("command.resetguild", new GuildResetCommand());
	}
}
