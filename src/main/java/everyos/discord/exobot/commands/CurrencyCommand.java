package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class CurrencyCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length==0) {
			channel.send("Expected at least one parameter", true); return;
        }

		UserObject invoker = UserHelper.getUserData(guild, message.getAuthorAsMember());

        if (args[0].equals("balance")||args[0].equals("bal")) {
            channel.send("Your current balance is: "+invoker.money+ " feth", true);
            return;
        } else if (args[0].equals("give")) {
            if (args.length<3) {
                channel.send("Usage: <command> give @user amount", true); return;
            }
            int amount = Integer.parseInt(args[2]);
            if (amount>invoker.money) {
                channel.send("You can't give money you don't have!\nAre you trying to build debt or something?", true);
                return;
            }
            if (!UserHelper.isUserId(args[1])) {
                if (args[1].equals("@everyone")||args[1].equals("@here")) {
                    channel.send("You can't just give everybody your money!", true);
                } else channel.send("I don't recognize this user!", true);
                return;
            }
            UserObject user = UserHelper.getUserData(guild, UserHelper.parseUserId(args[1]));
            if (user.id == invoker.id) {
                channel.send("Very funny", true); return;
            }
            invoker.money-=amount;
            user.money+=amount;
            if (user.id.equals(UserHelper.getUserId(Statics.client.getSelf().block()))) {
                channel.send("Thank you very much!", true);
            } else {
                channel.send("Money sent"+(user.isBot()?" to the bot":" to the user"), true);
            }
            StaticFunctions.save();
        } else if (args[0].equals("daily")) {
            long time = System.currentTimeMillis()/1000;
            long timeleft = 24*60*60-(time-invoker.dailytimestamp);
            if (timeleft<=0) {
                invoker.dailytimestamp = time;
                invoker.money+=100;
                StaticFunctions.save();
                channel.send("Daily prize credited 100 feth to account", true);
            } else {
                channel.send("You can only run this once a day, dummy!", true);
                long tlc = timeleft;
                long hours = Math.floorDiv(tlc, 60*60);
                tlc=tlc%(60*60);
                long minutes = Math.floorDiv(tlc, 60);
                long seconds = tlc%60;
                channel.send(hours+"h, "+minutes+"m, "+seconds+"s left", true);
            }
            return;
        } else {
            channel.send("Unsupported subcommand", true); return;
        }
	}

	@Override public String getHelp() {
		return "<command>[args+] Invokes commands on the currency system";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Fun;
	}
	
	@Override public String getFullHelp() {
        return "**<command>** Can be give, balance, or work\n"+
            "**[args+]** Run <balance/rate> subcommand for additional usage";
	}
}
