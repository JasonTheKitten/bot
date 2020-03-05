package everyos.discord.bot.commands.fun;

import everyos.discord.bot.commands.ICommand;

public abstract class AImageCommand implements ICommand {
    abstract String[] getImages();
}