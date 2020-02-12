package everyos.discord.exobot.commands;

import java.awt.Color;
import java.lang.reflect.Field;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class StyleRoleCommand implements ICommand {
    @Override public void execute(Message message, String argument) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length==0) {
			channel.send("Expected at least one parameter", true); return;
        }

        UserObject invoker = UserHelper.getUserData(guild, message.getAuthorAsMember());
        
        if (args[0].equals("enable")||args[0].equals("disable")) {
            if (!invoker.isOpted()) {
                channel.send("User is not opted to use this command", true); return;
            }
            guild.styledRolesEnabled=(args[0].equals("enable"))?true:false;
            StaticFunctions.save();

            channel.send("Updated configurations!", true); return;
        }

        if (args.length<2) {
            channel.send("Check usage", true); return;
        }

        if (!guild.styledRolesEnabled) {
            channel.send("This feature is disabled! Ask an opted user to enable it!", true); return;
        }

        guild.requireGuild().guild.createRole(role->{
            Color color = null;
            
            //if (args[0].substring(0, 1).equals("#")) {
            try {
                Field f = Color.class.getField(args[0].toLowerCase());
                color = (Color) f.get(null);
            } catch (Exception e) {e.printStackTrace();}
            //}

            role
                .setName(args[1])
                .setReason("Create with role styling command")
                .setColor(color);
        }).subscribe(role->{
            invoker.user.addRole(role.getId()).subscribe();
            channel.send("Gave you the role!", true);
        });
    }

    @Override public String getHelp() {
        return "<enable/disable/<color><name>> Gives you a styled role";
    }

    @Override
    public String getFullHelp() {
        return "**[enable/disable]** Enable or disable this command\n"+
            "**<color><name>** Creates a styled rol of name *name* and color *color*";
    }

    @Override
    public COMMANDS getType() {
        return COMMANDS.Fun;
    }

}