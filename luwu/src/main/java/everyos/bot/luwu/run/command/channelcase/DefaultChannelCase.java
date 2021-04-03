package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.battle.BattleCommands;
import everyos.bot.luwu.run.command.modules.chatlink.setup.LinkSetupCommands;
import everyos.bot.luwu.run.command.modules.configuration.ConfigurationCommands;
import everyos.bot.luwu.run.command.modules.easteregg.EasterEggCommands;
import everyos.bot.luwu.run.command.modules.fun.FunCommands;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import everyos.bot.luwu.run.command.modules.leveling.LevelCommands;
import everyos.bot.luwu.run.command.modules.logging.LogsCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.run.command.modules.music.MusicCommands;
import everyos.bot.luwu.run.command.modules.oneword.OneWordCommands;
import everyos.bot.luwu.run.command.modules.privacy.PrivacyCommands;
import everyos.bot.luwu.run.command.modules.role.RoleCommands;
import everyos.bot.luwu.run.command.modules.starboard.StarboardCommands;
import everyos.bot.luwu.run.command.modules.suggestions.SuggestionCommands;
import everyos.bot.luwu.run.command.modules.utility.UtilityCommands;
import everyos.bot.luwu.run.command.modules.welcome.WelcomeCommands;
import reactor.core.publisher.Mono;

public class DefaultChannelCase extends CommandChannelCase {
	private static DefaultChannelCase instance;
	private CommandContainer commands;
	
	public DefaultChannelCase() {
		this.commands = new CommandContainer();
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		LogsCommands.installTo(commands);
		
		commands.category("fun");
		MusicCommands.installTo(commands);
		FunCommands.installTo(commands);
		LevelCommands.installTo(commands);
		
		commands.category("info");
		InfoCommands.installTo(commands);
		
		commands.category("privacy");
		PrivacyCommands.installTo(commands);
		
		commands.category("configuration");
		ConfigurationCommands.installTo(commands);
		
		commands.category("channel");
		LinkSetupCommands.installTo(commands);
		OneWordCommands.installTo(commands);	
		SuggestionCommands.installTo(commands);
		StarboardCommands.installTo(commands);
		
		commands.category("utility");
		UtilityCommands.installTo(commands);
		RoleCommands.installTo(commands);
		WelcomeCommands.installTo(commands);
		
		
		commands.category(null);
		EasterEggCommands.installTo(commands);
		BattleCommands.installTo(commands);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(data, parser).then();
	}
	
	@Override
	public CommandContainer getCommands() {
		return commands;
	}
	
	@Override
	public String getID() {
		return "command.channelcase.default";
	}

	public static DefaultChannelCase get() {
		if (instance==null) instance = new DefaultChannelCase();
		return instance;
	}
}
