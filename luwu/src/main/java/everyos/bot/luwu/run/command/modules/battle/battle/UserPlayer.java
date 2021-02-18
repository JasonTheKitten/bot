package everyos.bot.luwu.run.command.modules.battle.battle;

import java.util.ArrayList;
import java.util.function.Consumer;

import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class UserPlayer implements Player {
	
	private User user;
	private ChannelTextInterface userChannel;
	private Locale locale;

	private UserPlayer(User user, Locale locale, ChannelTextInterface userChannel) {
		this.user = user;
		this.locale = locale;
		this.userChannel = userChannel;
	}

	@Override
	public Move[][] getAvailableMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return user.getHumanReadableID();
	}

	@Override
	public int getInitialHP() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Mono<Action> selectMove(Move[] moves, Item[] items) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Mono<Player> selectPlayer(Player[] players) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Void> notify(String label, String... args) {
		return notify(spec->{
			spec.addText(label, args);
		});
	}

	@Override
	public Mono<Void> notify(Consumer<NotifySpec> fun) {
		ArrayList<Tuple<String, String[]>> labelTup = new ArrayList<>();
		fun.accept(new NotifySpecImp(labelTup));
		
		StringBuilder built = new StringBuilder();
		for (int i=0; i<labelTup.size(); i++) {
			Tuple<String, String[]> tup = labelTup.get(i);
			built.append(locale.localize(tup.getT1(), tup.getT2()));
		}
		return userChannel.send(built.toString()).then();
	}
	
	public static Mono<UserPlayer> getPlayer(User user, Locale locale) {
		//TODO: Figure out the locale ourselves
		return
			user.getPrivateChannel()
			.map(channel->channel.getInterface(ChannelTextInterface.class))
			.map(channel->new UserPlayer(user, locale, channel));
	}
	
	private static class NotifySpecImp implements NotifySpec {
		private ArrayList<Tuple<String, String[]>> labelTup;

		public NotifySpecImp(ArrayList<Tuple<String, String[]>> labelTup) {
			this.labelTup = labelTup;
		}

		@Override
		public void addText(String label, String... args) {
			labelTup.add(Tuple.of(label, args));
		}
	}

}
