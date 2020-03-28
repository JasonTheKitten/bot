package everyos.discord.bot.command.fun;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

public class HugCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		String[] hugs = new String[] { //TODO: Move to file
				"https://i.giphy.com/media/9JnRMIFMYAKpaHRXRF/200.gif",
				"https://i.giphy.com/media/HwOGA0ZvBXP5C/200.gif",
				"https://i.giphy.com/media/8tpiC1JAYVMFq/200.gif",
				"https://i.giphy.com/media/ViKfjpmrS8Cf6/200.gif",
				"https://i.giphy.com/media/1FkCqpyObTuo0/200.gif",
				"https://i.giphy.com/media/gl8ymnpv4Sqha/200.gif",
				"https://i.giphy.com/media/3M4NpbLCTxBqU/200.gif",
				"https://i.giphy.com/media/jMGxhWR7rtTNu/200.gif",
				"https://i.giphy.com/media/j0AsV8zMp1JJQGSbLS/200.gif",
				"https://i.giphy.com/media/l4FGpP4lxGGgK5CBW/200.gif",
				"https://i.giphy.com/media/lXiRKBj0SAA0EWvbG/200.gif",
				"https://i.giphy.com/media/3bdGMdcWTIto24a8vK/200.gif",
				"https://i.giphy.com/media/gnXG2hODaCOru/200.gif",
				"https://i.giphy.com/media/JdPmb95rKeDn2/200.gif",
				"https://i.giphy.com/media/Ilkurs1e3hP0c/200.gif",
				"https://i.giphy.com/media/UwaByp0aMg6BO/200.gif",
				"https://i.giphy.com/media/dfgm6lXR4pV8k/200w.gif",
				"https://i.giphy.com/media/88usq9ke3jvlm/200.gif",
				"https://i.giphy.com/media/2GnS81AihShS8/200.gif",
				"https://i.giphy.com/media/TT9sxutF6IlTW/200.gif",
				"https://i.giphy.com/media/fWrorpy7Jrlvi/200.gif",
				"https://i.giphy.com/media/fvN5KrNcKKUyX7hNIA/200.gif",
				"https://i.giphy.com/media/1yjLtavDnVGaMUdL43/200.gif",
				"https://i.giphy.com/media/gGpkYyAEQbfjNEwLur/200.gif",
				"https://i.giphy.com/media/J1S1lQ3bCNj1RYN0b9/200.gif",
				"https://i.giphy.com/media/f487AxBVvjt8FcceRb/200.gif",
				"https://i.giphy.com/media/2ZZN9mOePSw9QEms2v/200.gif",
				"https://i.giphy.com/media/nt0vgOawKunde/200.gif",
				"https://i.giphy.com/media/7J86GZZBkd8swc0IOV/200.gif",
				"https://i.giphy.com/media/Yj8onktWOcxnXhcBUP/200.gif",
				"https://i.giphy.com/media/Tja5U6KuWPVGE/200.gif",
				"https://i.giphy.com/media/Bq6r8jnThNGixJY8c0/200.gif",
				"https://i.giphy.com/media/p3rk2Uj69XljusB6TO/200.gif",
				"https://i.giphy.com/media/KDteE5sy0H1gW8kxQV/200.gif",
				"https://i.giphy.com/media/HvitTO6AhBGCY/200.gif",
				"https://i.giphy.com/media/sX755wvr2Q6gE/200.gif",
				"https://i.giphy.com/media/h9NEP5r1O1z56/200.gif"
		};
		return message.getChannel().flatMap(channel->{ //TODO: X sent Y a hug
			return channel.createEmbed(embed->{
				embed.setDescription("You've been sent a hug!");
				embed.setImage(hugs[(int)Math.round(Math.random()*hugs.length)]);
				embed.setFooter("Powered by Giphy", null);
			});
		});
	}
}
