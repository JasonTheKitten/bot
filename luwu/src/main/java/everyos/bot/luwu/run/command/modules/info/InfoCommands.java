package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.luwu.core.command.CommandContainer;

public class InfoCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.donate", new DonateCommand());
		commands.registerCommand("command.help", new HelpCommand());
		commands.registerCommand("command.ping", new PingCommand());
		commands.registerCommand("command.vote", new DBLVoteCommand());
		commands.registerCommand("command.website", new WebsiteCommand());
		commands.registerCommand("command.support", new SupportCommand());
	}
}
