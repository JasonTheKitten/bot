package everyos.discord.exobot.cases;

import com.google.gson.JsonObject;

import everyos.discord.exobot.objects.ChannelObject;

public class SuggestionChannelCaseData implements IChannelCaseData {
	public String voteChannel;
	
	public SuggestionChannelCaseData(ChannelObject voteChannel) {
		this.voteChannel = voteChannel.channel.getId().asString();
	}

	public SuggestionChannelCaseData(JsonObject save) {
		this.voteChannel = save.get("votechannel").getAsString();
	}

	@Override public String serializeSave() {
		return "{\"votechannel\":\""+this.voteChannel+"\"}";
	}
}
