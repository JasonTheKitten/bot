package everyos.bot.luwu.run.command;

import java.util.function.Function;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import reactor.core.publisher.Mono;

public abstract class CommandBase implements Command {
	private final String id;
	
	@SuppressWarnings("unused")
	private RateLimit rateLimit;
	private Function<Client, Boolean> checkSupportedFunc;
	private int requiredUserPerms;

	public CommandBase(String id, Function<Client, Boolean> checkSupportedFunc, int requiredBotPerms, int requiredUserPerms) {
		this.id = id;
		this.checkSupportedFunc = checkSupportedFunc;
		this.requiredUserPerms = requiredUserPerms;
	}
	
	@Override
	public Mono<Void> run(CommandData data, ArgumentParser parser) {
		Member invoker = data.getInvoker();
		return invoker.getPermissions().flatMap(permissions->{
			int difference = ChatPermission.contains(requiredUserPerms, permissions);
			if (difference == 0) {
				return Mono.empty();
			} else {
				String[] missingPerms = Member.convertPermsToNames(difference);
				StringBuilder builder = new StringBuilder();
				for (String perm: missingPerms) {
					builder.append(", "+data.getLocale().localize(perm));
				}
				return Mono.error(new TextException(data.getLocale().localize(
					"command.error.missinguserperm", "permissions", builder.toString().substring(2))));
			}
		}).then(execute(data, parser));
	}
	
	@Override
	public String getID() {
		return this.id;
	}
	
	@Override
	public boolean isSupported(Client client) {
		return checkSupportedFunc.apply(client);
	}
	
	protected <T> Mono<T> expect(Locale locale, ArgumentParser parser, String error) {
		String got = parser.getRemaining();
		if (got.isEmpty()) {
			got = locale.localize("command.error.nothing");
		}
		return Mono.error(new TextException(locale.localize("command.error.usage",
			"expected", locale.localize(error),
			"got", got)));
	}
}
