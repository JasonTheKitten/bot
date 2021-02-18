package everyos.bot.luwu.run.command.modules.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import everyos.bot.chat4j.entity.ChatColor;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.CommandEntry;
import everyos.bot.luwu.core.command.GroupCommand;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class HelpCommand extends CommandBase {
	public HelpCommand() {
		super("command.help");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		String commandName = parser.getRemaining();
		return resolveCommand(data, commandName.strip(), locale).flatMap(tup->{
			return showHelpForCommand(data.getChannel(), tup.getT1(), tup.getT2(), locale);
		});
	}

	private Mono<Tuple<Command, String>> resolveCommand(CommandData data, String input, Locale locale) {
		return data.getBotEngine().getChannelCase(data).flatMap(channelCase->{
			Command resolved = channelCase;
			String remaining = input;
			while (!remaining.isEmpty()) {
				if (!(resolved instanceof GroupCommand)) {
					return Mono.just(Tuple.of(null, null));
				}
				if (remaining.startsWith(">")) {
					return Mono.just(Tuple.of(resolved, remaining.substring(1, remaining.length())));
				}
				int space = remaining.indexOf(" ");
				space=space==-1?remaining.length():space;
				//TODO: Don't show help for hidden commands
				resolved = ((GroupCommand) resolved).getCommands().getCommand(remaining.substring(0, space), locale);
				
				if (space==remaining.length()) {
					remaining = "";
				} else {
					remaining = remaining.substring(space+1, remaining.length()).strip();
				}
			}
			
			return Mono.just(Tuple.of(resolved, null));
		});
	}
	
	public static Mono<Void> showHelpForCommand(Channel channel, Command command, String category, Locale locale) {
		if (command == null) {
			return Mono.error(new TextException(locale.localize("command.help.nosuchcommand")));
		}
		
		if (command instanceof GroupCommand) {
			return showGroupCommandHelp(channel, (GroupCommand) command, category, locale);
		}
		
		String id = command.getID();
		if (id==null) {
			return Mono.error(new TextException("command.help.error.idmissing"));
		}
		
		ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
		
		return textGrip.send(spec->{
			spec.setEmbed(embedSpec->{
				//TODO: Localize
				embedSpec.setTitle("Extended Help - "+locale.localize(id));
				embedSpec.setColor(ChatColor.of(15, 120, 35));
				
				embedSpec.addField("Help", getOrUndocumented(id+".help", locale), false);	
				embedSpec.addField("Usage", getOrUndocumented(id+".help.extended", locale), false);
				
				embedSpec.setFooter("Luwu is written by EveryOS");
			});
		}).then();
	}

	public static Mono<Void> showGroupCommandHelp(Channel channel, GroupCommand command, String categoryb, Locale locale) {
		if (command == null) {
			return Mono.error(new TextException(locale.localize("command.help.error.nosuchcommand")));
		}
		
		String category = categoryb==null?"":categoryb;
		
		String id = command.getID();
		
		ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
		
		//TODO: Command groups
		
		CommandEntry[] entries = command.getCommands().getAll();
		HashMap<String, ArrayList<CommandEntry>> groups = new HashMap<>();
		
		for (CommandEntry subcommandEntry: entries) {
			if (subcommandEntry.getCategory()==null) continue;
			groups
				.computeIfAbsent(subcommandEntry.getCategory(), key->new ArrayList<>())
				.add(subcommandEntry);
		}
		
		List<CommandEntry> group = groups.getOrDefault(category.isEmpty()?"default":category, new ArrayList<>());
		CommandEntry[] groupArray = group.toArray(new CommandEntry[group.size()]);
		String helpTextInitial = constructHelpForCommands(groupArray, locale);
		
		StringBuilder listingsBuilder = new StringBuilder();
		if (category.equals("")) {
			for (String key: groups.keySet()) {
				if (key.equals("default")) continue;
				//TODO
				listingsBuilder.append("\n**"+key+"** - `help >"+key+"`");
			}
		}
		String listings = listingsBuilder.toString().isEmpty()?"":"\n"+listingsBuilder.toString();
		
		String helpText = !(listings.isEmpty()&&helpTextInitial.isEmpty())?
			helpTextInitial:
			"\n\n"+locale.localize("command.help.error.nosubcommands");
		
		
		return textGrip.send(spec->{
			spec.setEmbed(embedSpec->{
				String desc = getOrUndocumented(id==null?null:(id+".help.extended"), locale);
				//TODO: Localize
				embedSpec.setTitle("Help"+
					(id==null||!locale.canLocalize(id)?"":" - "+locale.localize(id))+
					(category.isEmpty()?"":(" - >"+category)));
				if (listings.isEmpty()) {
					embedSpec.setColor(ChatColor.of(50, 80, 145));
				} else {
					embedSpec.setColor(ChatColor.of(80, 0, 70));
				}
				embedSpec.setDescription(desc+listings+helpText);
				embedSpec.setFooter("Luwu is written by EveryOS");
			});
		}).then();
	};
	
	private static String constructHelpForCommands(CommandEntry[] entries, Locale locale) {
		StringBuilder helpBuilder = new StringBuilder("\n");
		for (CommandEntry subcommandEntry: entries) {
			if (subcommandEntry.getCategory()==null) continue;
			String subcommandTitle = locale.localize(subcommandEntry.getLabel());
			String scid = subcommandEntry.getRaw().getID();
			String subcommandDesc = getOrUndocumented(scid==null?null:(scid+".help"), locale);
			helpBuilder.append("\n**"+subcommandTitle+"** "+subcommandDesc);
		}
		
		if (entries.length==0) {
			return "";
		}
		
		return helpBuilder.toString();
	}
	
	private static String getOrUndocumented(String label, Locale locale) {
		if (label==null || !locale.canLocalize(label)) {
			return locale.localize("command.help.error.undocumented");
		}
		
		return locale.localize(label);
	}
}
