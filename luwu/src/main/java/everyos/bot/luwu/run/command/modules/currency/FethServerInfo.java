package everyos.bot.luwu.run.command.modules.currency;

public class FethServerInfo {
	private boolean currencyEnabled;

	public FethServerInfo(boolean currencyEnabled) {
		this.currencyEnabled = currencyEnabled;
	}
	
	public boolean getCurrencyEnabled() {
		return currencyEnabled;
	}
}
