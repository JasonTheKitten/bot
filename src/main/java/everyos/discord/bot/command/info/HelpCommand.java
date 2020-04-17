package everyos.discord.bot.command.info;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandAlias;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.HelpCommandHelp, ehelp = LocalizedString.HelpCommandExtendedHelp, category=CategoryEnum.Info)
public class HelpCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			ICommand command = data.channelcase;
			String group = null;
			while (!parser.isEmpty()) {
				String arg = parser.eat();
				if (command instanceof IGroupCommand) {
					if (arg.startsWith(">") && arg.length()>1) {
						group = arg.substring(1).toUpperCase();
						break;
					}
					
					command = ((IGroupCommand) command).getCommands(data.locale.locale).get(arg);
				} else {
					return channel.createMessage(data.localize(LocalizedString.NoSuchCommand));
				}
				
				if (command == null) return channel.createMessage(data.localize(LocalizedString.NoSuchCommand));
			}
			
			return showHelp(command, group, channel, data, argument);
		});
	}
	
	public Mono<?> showHelp(ICommand commandu, String group, MessageChannel channel, CommandData data, String header) {
		ICommand command = commandu;
		while (commandu instanceof CommandAlias) commandu = ((CommandAlias) commandu).parent;
		
		Help defaultHelp = new Help() {
			@Override public Class<? extends Annotation> annotationType() { return Help.class; }
			@Override public LocalizedString help() { return LocalizedString.Undocumented; }
			@Override public LocalizedString ehelp() { return LocalizedString.Undocumented; }
			@Override public CategoryEnum category() { return CategoryEnum.NULL; }
		};
		if (command instanceof IGroupCommand) {
			HashMap<String, ICommand> commands = ((IGroupCommand) command).getCommands(data.locale.locale);
			HashMap<String, ICommand> acommands = new HashMap<String, ICommand>();
			ArrayList<CategoryEnum> groups = new ArrayList<CategoryEnum>();
			String fgroup = group;
			commands.forEach((name, cmdu)->{
				ICommand cmd = cmdu;
				while (cmdu instanceof CommandAlias) cmdu = ((CommandAlias) cmdu).parent;
				Help help = cmd.getClass().getAnnotation(Help.class);
				if (help == null) help = defaultHelp;
				boolean matches = (fgroup==null)||
					(help.category()!=CategoryEnum.NULL&&help.category().toString().toUpperCase().equals(fgroup));
				if (help.category()!=CategoryEnum.NULL && !groups.contains(help.category())) groups.add(help.category());
				if (matches) acommands.put(name, cmdu);
			});
			return channel.createEmbed(spec->{
				if (fgroup==null && !groups.isEmpty()) {
					StringBuilder ctgmsg = new StringBuilder();
					for (CategoryEnum cgroup: groups) {
						if (header.isEmpty()) {
							ctgmsg.append(("**%s** - `help >%s`\n").replaceAll("%s", cgroup.toString().toLowerCase()));
						} else ctgmsg.append(String.format("**%s** - `help %s >%s`\n", cgroup.toString(), header, cgroup.toString().toLowerCase()));
					}
					spec.setTitle(header + " - Help (Groups)");
					spec.setDescription(ctgmsg.toString());
					return;
				}
				
				StringBuilder helpmsg = new StringBuilder();
				acommands.forEach((name, cmd)->{
					String desc;
					if (cmd instanceof CommandAlias) {
						desc = data.locale.localize(LocalizedString.CommandAlias, FillinUtil.of("command", ((CommandAlias) cmd).pname));
					} else {
						Help help = cmd.getClass().getAnnotation(Help.class);
						if (help == null) help = defaultHelp;
						desc = data.localize(help.help());
					}
					helpmsg.append(String.format("**%s** %s\n", name, desc));
				});
				
				spec.setTitle(header + " - Help");
				spec.setDescription(helpmsg.toString());
			});
		} else {
			return channel.createEmbed(spec->{
				Help help = command.getClass().getAnnotation(Help.class);
				if (help == null) help = defaultHelp;
				
				spec.setTitle(header + " - Extended Help");
				spec.addField("Help", data.localize(help.help()), false);
				spec.addField("Usage", data.localize(help.ehelp()), false);
			});
		}
	}
}