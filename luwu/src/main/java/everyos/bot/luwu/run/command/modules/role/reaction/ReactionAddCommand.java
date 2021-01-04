package everyos.bot.luwu.run.command.modules.role.reaction;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.ReactionID;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class ReactionAddCommand extends CommandBase {

	public ReactionAddCommand() {
		super("command.role.reaction.add");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			parseArguments()
			.flatMap(args->createReaction(args));
	}

	

	private Mono<Tuple<Long, Tuple<Long, ReactionID>[]>> parseArguments() {
		/*
		 reaction add [mid]
		 	[emoji] [roleid]
		 	[emoji] [roleid]
		 */
		
		
		return Mono.empty();
	}
	
	private Mono<Void> createReaction(Tuple<Long, Tuple<Long, ReactionID>[]> args) {
		return Mono.empty();
	}

}
