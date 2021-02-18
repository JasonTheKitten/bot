package everyos.bot.luwu.run.command.modules.battle;

import java.util.ArrayList;
import java.util.HashMap;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.battle.battle.Battle;
import everyos.bot.luwu.run.command.modules.battle.battle.UserPlayer;
import reactor.core.publisher.Mono;

public class BattleCommand extends CommandBase {
	private static final BattleCommand instance;
	@SuppressWarnings("unused")
	private static final HashMap<BotEngine, ArrayList<Battle>> battles;
	
	static {
		instance = new BattleCommand();
		battles = new HashMap<>();
	}
	public static BattleCommand get() {
		return instance;
	}
	

	private BattleCommand() {
		super("command.battle");
	}
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return UserPlayer.getPlayer(data.getInvoker(), data.getLocale()).flatMap(player->{
			Battle battle = new Battle();
			return battle.join(player).then(battle.start());
		});
	}
}
