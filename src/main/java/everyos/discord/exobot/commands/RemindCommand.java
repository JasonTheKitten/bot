package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GlobalUserObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.ReminderObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class RemindCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());

        int semi = argument.indexOf(";");
        if (semi==-1) {
            channel.send("Parameters should include a semicolon", true); return;
        }

        String[] args = StringUtil.sub(argument, 0, semi).split(" ");

        if (args.length==0||!UserHelper.isUserId(args[0])) {
            channel.send("Expected first param to be user ping!", true); return;
        }

        String txt = StringUtil.sub(argument, semi+1).replace("@", "");
        if (txt.equals("")) {
            channel.send("Reminder body not specified!", true); return;
        }

        long time = -1; //TODO: Calc
        if (args.length>1) {
            time = 0;
            for (int i=1; i<args.length; i++) {
                String arg = args[i];

                if (arg.endsWith("d")) {
                    time += (double)(24*60*Integer.parseInt(StringUtil.sub(arg, 0, arg.length()-1)));
                } else if (arg.endsWith("m")) {
                    time += (double)(60*Integer.parseInt(StringUtil.sub(arg, 0, arg.length()-1)));
                } else if (arg.endsWith("s")) {
                    time += (double)(Integer.parseInt(StringUtil.sub(arg, 0, arg.length()-1)));
                } else {
                    channel.send("Unrecognized argument: "+i, false); return;
                }
            }
        }
        if (time<1&&args.length!=1) {
            channel.send("Time must be at least 1 second!", true); return;
        }

        GlobalUserObject user = UserHelper.getGlobalUserData(args[0]);

        synchronized(user.reminders) {
            ReminderObject obj;
            if (time == -1) {
                obj = new ReminderObject(-1, guild.id, channel.id, user.id, txt);
            } else {
                obj = ReminderObject.inSeconds(time, guild.id, channel.id, user.id, txt);
            }
            
            user.reminders.add(obj);
            StaticFunctions.save();
        }
	}

	@Override public String getHelp() {
		return "<user><days/login><text> Reminds a user some text";
	}

	@Override public String getFullHelp() {
		return 
				"**<user>** user to remind\n"+
                "**[time]** - When user should be reminded (integer followed by d, s, or m). If not specified, is upon a presence set to idle or active\n"+
                "<;> Semicolon seperator\n"+
				"**<text>** text to remind user";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Info;
	}
}
