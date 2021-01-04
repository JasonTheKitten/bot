package everyos.bot.luwu.run.command;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.GroupCommand;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public abstract class MultiCommand extends CommandBase implements GroupCommand {
	private String unlocalizedName;

	public MultiCommand(String unlocalizedName) {
		super(unlocalizedName);
		this.unlocalizedName = unlocalizedName;
	}
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		if (parser.isEmpty()) {
			//TODO: Show help instead
        	return data.getChannel().getInterface(ChannelTextInterface.class)
        		.send(data.getLocale().localize("command.error.missingsubcommand"))
        		.then();
		}
		
		String cmd = parser.eat();
        
		Command command = getCommands().getCommand(cmd, data.getLocale()); //TODO: Detect preferred locale
        
        if (command==null) {
        	Locale locale = data.getLocale();
        	return Mono.error(new TextException(locale.localize("command.error.invalidsubcommand",
        		"command", cmd,
        		"parent", locale.localize(unlocalizedName))));
        }
        	
	    
        return command.execute(data, parser);
	}
}
