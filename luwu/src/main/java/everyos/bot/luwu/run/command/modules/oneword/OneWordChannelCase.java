package everyos.bot.luwu.run.command.modules.oneword;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.channelcase.CommandChannelCase;
import everyos.bot.luwu.run.command.modules.info.HelpCommand;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.run.command.modules.oneword.moderation.OneWordModerationCommands;
import reactor.core.publisher.Mono;

public final class OneWordChannelCase extends CommandChannelCase {
	
	private static OneWordChannelCase instance = new OneWordChannelCase();
	private static JsonObject words;
	
	private OneWordChannelCase() {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream("whitelist.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			words = JsonParser.parseReader(reader).getAsJsonObject();
			reader.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static OneWordChannelCase get() {
		return instance;
	}
	
	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.category("default");
		OneWordModerationCommands.installTo(commands);
		
		commands.category("info");
		commands.registerCommand("command.help", new HelpCommand());
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		
		return commands;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(data, parser)
			.filter(v->!v)
			.flatMap(v->submitWord(data, parser));
	}
	
	@Override
	public String getID() {
		return "command.oneword.channelcase";
	}

	private Mono<Void> submitWord(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		String word = parser.getRemaining();
		if (!words.has(word.toLowerCase())) {
			return Mono.error(new TextException(locale.localize("command.oneword.unrecognizedword")));
		}
		
		return data.getChannel().as(OneWordChannel.type).flatMap(channel->{
			return channel.getInfo().flatMap(info->{
				UserID uid = data.getInvoker().getID();
				if (uid.equals(info.getLastUser())) {
					return Mono.error(new TextException(locale.localize("command.oneword.onlyoneword")));
				}
				
				String newMessage = channel.delimit(info.getMessage()+(words.get(word.toLowerCase()).getAsInt()==1?" "+word:word));
				
				return channel.edit(spec->{
					spec.setLastUser(uid);
					spec.setMessage(newMessage);
				}).then(channel.getInterface(ChannelTextInterface.class).send(newMessage));
			}).then();
		});
	}
}
