package everyos.bot.luwu.run.command.modules.battle.battle;

import java.util.function.Consumer;

import reactor.core.publisher.Mono;

public interface Player {
	public Move[][] getAvailableMoves();
	public String getName();
	public int getInitialHP();
	
	public Mono<Action> selectMove(Move[] moves, Item[] items);
	public Mono<Player> selectPlayer(Player[] players);
	public Mono<Void> notify(String label, String... args);
	public Mono<Void> notify(Consumer<NotifySpec> fun);
}
