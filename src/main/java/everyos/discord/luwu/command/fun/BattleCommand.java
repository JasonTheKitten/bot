package everyos.discord.luwu.command.fun;

import java.util.ArrayList;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizationProvider;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
@Help(category=CategoryEnum.Fun, help=LocalizedString.BattleCommandHelp)
public class BattleCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return startBattle(data, message.getAuthor().get());
	}
	
	public Mono<?> startBattle(CommandData data, User user) {
		//Let's write some basic logic!
		//1. Find a player, or generate a player
		//2. Notify all players the battle has started
		//3. Notify players that they are waiting
		//4. Prompt a player to make a move
		//5. If they make it, apply it
		//6. Else, they die
		//7. Reward players
		//To write this, I will use top down
		
		return findBattle().flatMap(battle->battle.join(new PlayerBattleCharacter(data, user))); //With battle constructor
	}

	private Mono<Battle> findBattle() {
		//For now let's just generate without searching
		Battle battle = createBattle();
		return battle.join(new NPCBattleCharacter()).then(Mono.just(battle));
	}
	
	private Battle createBattle() {
		return new Battle();
	}
	
	public static class Battle {
		public ArrayList<BattleCharacter> players = new ArrayList<BattleCharacter>();
		
		public Mono<?> join(BattleCharacter player) {
			players.add(player);
			if (players.size() == 1) return waitingMessage();
			return generateBattleMessages().then(player.indicateTurn());
		}

		private Mono<?> generateBattleMessages() {
			return Mono.empty();
		}

		private Mono<?> waitingMessage() {
			return Mono.empty();
		}
	}
	
	public static interface BattleCharacter {
		public Mono<?> indicateTurn();
	}
	
	public class NPCBattleCharacter implements BattleCharacter {
		@Override public Mono<?> indicateTurn() {
			return null;
		}
	}
	
	public static class PlayerBattleCharacter implements BattleCharacter {
		private LocalizationProvider locale;
		private User user;
		private CommandData data;

		public PlayerBattleCharacter(CommandData data, User user) {
			this.user = user;
			this.locale = new LocalizationProvider(Localization.en_US);
			this.data = data;
		}

		@Override public Mono<?> indicateTurn() {
			return createTurnMessage();
		}

		private Mono<?> createTurnMessage() {
			return user.getPrivateChannel().flatMap(channel->{
				return channel.createMessage(message->{
					message.setContent(locale.localize(LocalizedString.BattleYourTurn));
				}).flatMap(message->{
					return generateTurnMoves().flatMap(moves->createTurnMenu(message, moves));
				});
			});
		}
		
		private Mono<?> createTurnMenu(Message base, Object moves) {
			return base.edit(spec->{
				spec.setEmbed(embed->{
					embed.setTitle(locale.localize(LocalizedString.BattleTitle));
					embed.setDescription(//TODO: Localize
						"A - Moves\n"+
						"B - Items\n"+
						"C - Skip Turn\n"+
						"D - Surrender"
					);
				});
			}).flatMap(message->{
				//TODO: Use proper unicode
				Mono<?> ra = addReaction(message, "\uD83C\uDDE6", createMoveMenu(base, moves));
				Mono<?> rb = addReaction(message, "\uD83C\uDDE7", createItemsMenu(base, moves));
				Mono<?> rc = addReaction(message, "\uD83C\uDDE8", skipTurn());
				Mono<?> rd = addReaction(message, "\uD83C\uDDE9", surrender());
				
				return ra.and(rb).and(rc).and(rd);
			});
		}

		private Mono<?> surrender() {
			return null;
		}

		private Mono<?> skipTurn() {
			return null;
		}

		private Mono<?> createItemsMenu(Message base, Object moves) {
			return null;
		}

		private Mono<?> createMoveMenu(Message base, Object moves) {
			return base.edit(spec->{
				
			});
		}

		private Mono<Move[]> generateTurnMoves() {
			return Mono.just(new Move[4]); //We can provide default moves for now
		}
		private Mono<Spellbook[]> getSpellbooks() {
			return null;
		}
		private Mono<?> addReaction(Message message, String name, Mono<?> mono) {
			return data.bot.data.registerReaction(message, name, mono);
		}
		private void clearReactions() {
			
		}
	}
	
	public class Spellbook {
		
	}
	
	public class Move {
		
	}
}
