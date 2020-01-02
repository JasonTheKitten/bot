package everyos.discord.exobot.cases;

import everyos.discord.exobot.ChannelObject;

public class SuggestionChannelCaseData implements IChannelCaseData {
	public ChannelObject voteChannel;
	
	public SuggestionChannelCaseData(ChannelObject voteChannel) {
		this.voteChannel = voteChannel;
	}
}
