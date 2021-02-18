package everyos.bot.luwu.run.command.modules.battle.battle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import reactor.core.publisher.Mono;

public class Battle {
	private List<Player> players;

	public Battle() {
		players = Collections.synchronizedList(new LinkedList<>());
	}
	
	public Mono<Void> join(Player player) {
		return Mono.defer(()->{
			players.add(player);
			
			StringBuilder playerBuilder = new StringBuilder();
			if (players.isEmpty()) {
				playerBuilder.append("command.battle.playerspending");
			}
			
			return player.notify("command.battle.joined", "players", "jign");
		});
	}

	public Mono<Void> start() {
		return Mono.defer(()->{
			if (players.size()==1) {
				return players.get(0).notify("command.battle.error.nem");
			}
			if (players.size()==0) {
				return Mono.empty();
			}
			
			return nextTurn();
		});
	}

	private Mono<Void> nextTurn() {
		return Mono.defer(()->{
			if (players.size()==1) {
				//TODO
			}
			if (players.size()==0) {
				return Mono.empty();
			}
			
			Player player = players.remove(0);
			players.add(player);
			
			return player.selectMove(getMoves(player), new Item[] {})
				.flatMap(action->action.execute())
				.then(nextTurn());
		});
	}

	private Move[] getMoves(Player player) {
		
		return null;
	}
}
