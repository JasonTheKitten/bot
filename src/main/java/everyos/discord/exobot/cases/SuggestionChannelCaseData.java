package everyos.discord.exobot.cases;

import com.google.gson.JsonObject;

import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class SuggestionChannelCaseData implements IChannelCaseData {
	public String voteChannel;
	
	public SuggestionChannelCaseData(ChannelObject voteChannel) {
		this.voteChannel = voteChannel.id;
	}

	public SuggestionChannelCaseData(JsonObject save) {
		this.voteChannel = save.get("votechannel").getAsString();
	}

	@Override public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("votechannel", this.voteChannel);
        return save;
	}
}
