package everyos.discord.exobot.objects;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import everyos.discord.exobot.util.SaveUtil.JSONArray;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class PlaylistObject {
    public ArrayList<String> playlist;
    public String name;

    public PlaylistObject(String name) {
        this.name = name;
        this.playlist = new ArrayList<String>();
	}

	public PlaylistObject(JsonObject save) {
        this.name = save.get("name").getAsString();
        
        this.playlist = new ArrayList<String>();
        synchronized(this.playlist) {
            JsonArray pl = save.get("playlist").getAsJsonArray();
            pl.forEach(element->{
                this.playlist.add(element.getAsString());
            });
        } 
    }

    public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("name", name);

        JSONArray array = new JSONArray();
        synchronized(playlist) {
            playlist.forEach(url->array.put(url));
        }
        save.put("playlist", array);

        return save;
	}
}
