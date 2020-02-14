package everyos.discord.exobot.objects;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.SaveUtil.JSONArray;
import everyos.discord.exobot.util.SaveUtil.JSONObject;
import everyos.discord.exobot.util.UserHelper;

public class GlobalUserObject {
    public ArrayList<ReminderObject> reminders;
    public HashMap<String, PlaylistObject> playlists;
    public User user;
	public String id;

	public GlobalUserObject(User user) {
        this(UserHelper.getUserId(user));
		this.user = user;
    }
    
    public GlobalUserObject(String uid) {
        this.id = uid;
        if (this.id==null) Integer.valueOf("A");
        this.reminders = new ArrayList<ReminderObject>();
        this.playlists = new HashMap<String, PlaylistObject>();
	}

	public GlobalUserObject(JsonObject save) {
        this.id = save.get("id").getAsString();
        
        this.reminders = new ArrayList<ReminderObject>();
        JsonArray rem = save.get("reminders").getAsJsonArray();
        rem.forEach(element->{
            synchronized(reminders) {
                this.reminders.add(ReminderObject.fromSave(element.getAsJsonObject()));
            }
        });

        this.playlists = new HashMap<String, PlaylistObject>();
        if (save.has("playlists")) {
            JsonArray playo = save.get("playlists").getAsJsonArray();
            synchronized(reminders) {
                playo.forEach(playlistj->{
                    PlaylistObject playlist = new PlaylistObject(playlistj.getAsJsonObject());
                    this.playlists.put(playlist.name, playlist);
                });
            }
        }
    }
    
    public GlobalUserObject requireUser() {
        if (this.user==null) this.user = Statics.client.getUserById(Snowflake.of(this.id)).block();

        return this;
    }

	public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("id", id);

        JSONArray array = new JSONArray();
        synchronized(reminders) {
            int i = 0;
            while (i<reminders.size()) {
                if (reminders.get(i).fulfilled) {
                    reminders.remove(i);
                } else {
                    array.put(reminders.get(i).serializeSave());
                    i++;
                }
            }
        }
        save.put("reminders", array);

        final JSONArray narray = new JSONArray();
        synchronized(playlists) {
            playlists.forEach((k, v)->{
                narray.put(v.serializeSave());
            });
        }
        save.put("playlists", narray);

        return save;
    }
    
    public PlaylistObject getPlaylist(String name, boolean createIfNull) {
        if (playlists.get(name)!=null) {
            return playlists.get(name);
        } else if (!createIfNull) return null;
        PlaylistObject playlist = new PlaylistObject(name);
        playlists.put(name, playlist);
        
        return playlist;
    }
}
