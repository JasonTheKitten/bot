package everyos.discord.luwu.command;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class CommandAlias implements ICommand {
	public ICommand parent;
	public String pname;

	public CommandAlias(ICommand parent, String pname) {
		this.parent = parent;
		this.pname = pname;
	}
	
    @Override public Mono<?> execute(Message chain, CommandData data, String argument) {
        return parent.execute(chain, data, argument);
    }
}