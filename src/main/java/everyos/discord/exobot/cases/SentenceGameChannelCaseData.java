package everyos.discord.exobot.cases;

import com.google.gson.JsonObject;

import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class SentenceGameChannelCaseData implements IChannelCaseData {
    public String sentence;
    public long lastUser;
	
	public SentenceGameChannelCaseData() {
        sentence = "";
        lastUser = -1;
    }

	public SentenceGameChannelCaseData(JsonObject save) {
        this.sentence = save.get("sentence").getAsString();
        this.lastUser = save.get("lastuser").getAsLong();
	}

	@Override public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("sentence", this.sentence);
        save.put("lastuser", this.lastUser);
        return save;
	}
}
