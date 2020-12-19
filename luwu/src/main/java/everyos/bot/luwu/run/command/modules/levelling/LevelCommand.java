package everyos.bot.luwu.run.command.modules.levelling;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class LevelCommand implements Command {
	private CommandContainer commands;

	public LevelCommand() {
		this.commands = new CommandContainer();

        //Commands
        Command levelCheckCommand = new LevelCheckCommand();
        

        commands.registerCommand("command.level.check", levelCheckCommand);
        
	}
	
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		if (parser.isEmpty()) {
        	return data.getChannel().getInterface(ChannelTextInterface.class)
        		.send(data.getLocale().localize("command.error.missingsubcommand"))
        		.then();
		}
		
		String cmd = parser.eat();
        
		Command command = commands.getCommand(cmd, data.getLocale()); //TODO: Detect preferred locale
        
        if (command==null) {
        	Locale locale = data.getLocale();
        	return Mono.error(new TextException(locale.localize("command.error.invalidsubcommand",
        		"command", cmd,
        		"parent", locale.localize("command.level"))));
        }
        	
	    
        return command.execute(data, parser);
	}
}
