package everyos.discord.exobot.objects;

import java.util.TimerTask;

import com.google.gson.JsonObject;

import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;
import everyos.discord.exobot.util.UserHelper;

public class ReminderObject {
    public final long timestamp;
    public final String guildID;
    public final String channelID;
    public final String userID;
    public final String message;

    public boolean fulfilled = false;

    public ReminderObject(final long timestamp, final String guildID, final String channelID, final String userID, final String message) {
        this.timestamp = timestamp;
        this.guildID = guildID;
        this.channelID = channelID;
        this.userID = userID;
        this.message = message;

        if (this.timestamp!=-1) {
            long timeleft = this.timestamp*1000-System.currentTimeMillis();
            timeleft = (timeleft<0)?0:timeleft;
            Statics.timer.schedule(new TimerTask(){
                public void run() {
                    send(guildID, channelID, userID, message);
                    StaticFunctions.save();
                }
            }, timeleft);
        }
    }

    public void send(final String guildID, final String channelID, final String userID, final String message) {
        fulfilled = true;

        GuildObject guild = GuildHelper.getGuildData(guildID);
        UserObject user = UserHelper.getUserData(guild, userID).requireUser();

        ChannelObject channel = ChannelHelper.getChannelData(guild, channelID).requireChannel();
        channel.send("<@"+user.id+"> Reminder: "+message, true);
    }
    public void send() {
        send(guildID, channelID, userID, message);
    }

    public static ReminderObject inSeconds(long s, String guildID, String channelID, String userID, String message) {
        return new ReminderObject((System.currentTimeMillis()/1000)+s, guildID, channelID, userID, message);
    }
    
    public static ReminderObject fromSave(JsonObject save) {
        return new ReminderObject(
            save.get("timestamp").getAsLong(),
            save.get("guildid").getAsString(),
            save.get("channelid").getAsString(),
            save.get("userid").getAsString(),
            save.get("message").getAsString());
    }

    public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        
        synchronized(this) {
            save.put("guildid", this.guildID);
            save.put("channelid", this.channelID);
            save.put("userid", this.userID);
            save.put("message", this.message);
            save.put("timestamp", this.timestamp);
        }

        return save;
    }
}